/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.gameinit;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.amse.bomberman.util.ProtocolConstants;
import org.amse.bomberman.util.interpretator.*;

/**
 * Class that represents lobby chat. And ingame kill info.
 * @author Kirilchuk V.E.
 */
public class AsynchroChat {

    private final Game game;
    private final Map<Variable, Constant> map = new HashMap<Variable, Constant>();

    public AsynchroChat(Game game) {
        this.game = game;
    }

    /**
     * Adding message to chat in next notation:
     * <p>
     * playerName: message.
     * @param playerName nickName of player that added message.
     * @param message message to add to chat.
     */
    public void addMessage(String playerName, String message) {
        List<String> forClients = new ArrayList<String>();
        forClients.add(ProtocolConstants.CAPTION_GET_CHAT_MSGS);
        forClients.add(playerName + ": " + message);
        this.game.notifyGameSessions(forClients);
        /////////////
        expr(message);
        /////////////
    }
    
    public void addMessage(String message) {
        List<String> forClients = new ArrayList<String>();
        forClients.add(ProtocolConstants.CAPTION_GET_CHAT_MSGS);
        forClients.add("SuperMind: " + message);
        this.game.notifyGameSessions(forClients);
    }

    private void expr(String text) {
        String idName = null;

        StringBuilder res = new StringBuilder();

        int eqPos = text.indexOf('=');
        String buff;
        try {

            if (eqPos != -1) {
                String in = text.substring(0, eqPos);
                //получили имя идентификатора
                //или Parse Exception
                idName = getIdName(in);
                //после = должно быть выражение
                text = text.substring(eqPos + 1, text.length());
            }
            //обрабатываем выражение
            Expression expression = ExprBuilder.generate(text);
            //вычисляем выражение в заданном контексте
            Long value = expression.evaluate(map);
            buff = value.toString();
            //если это было выражение типа Identificator = Expression
            //запоминаем значение переменной в словаре!
            if (idName != null) {
                map.put(new Variable(idName), new Constant(value));
                //добавляем имя переменной в начало результата
                res.append(idName + " := ");
            } else {//иначе добавляем к результату выражение
                res.append(text + " = ");
            }
            //добавляем к результату значение выражения
            res.append(buff);

            // adding to chat
            addMessage(res.toString());
        } catch (Exception ex) {
            //TODO
        }
    }

    private static String getIdName(String in) throws ParseException{
	LexAnalyzer la = new LexAnalyzer(in);
	//Должно быть две лексемы
	//1)Identificator 2)EOTEXT
	Lexema lex = la.nextLex();
	if (lex.type!=Lexema.Type.IDENT){
	    throw new ParseException("Wrong Identificator in left side",0);
	} else {
	    String name = ((IdLexema)lex).name;
	    lex = la.nextLex();
	    if(lex!=Lexema.EOTEXT){
		throw new ParseException("Wrong Identificator in left side",0);
	    }else{
		return name;
	    }
	}

    }

    public void addKillMessage(String message) {
        List<String> forClients = new ArrayList<String>();
        forClients.add(ProtocolConstants.CAPTION_GET_CHAT_MSGS);
        forClients.add(message);
        this.game.notifyGameSessions(forClients);
    }

    /**
     * Always returns "No new messages."
     * @param chatID id of player that wants to get new messages.
     * @return list of messages or list with only item - "No new messages."
     */
    public List<String> getNewMessages(int chatID) {
        List<String> result = new ArrayList<String>();

        result.add("No new messages.");

        return result;
    }
}