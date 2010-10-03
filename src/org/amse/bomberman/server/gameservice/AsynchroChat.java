
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameservice;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.util.interpretator.*;

//~--- JDK imports ------------------------------------------------------------

import java.text.ParseException;
import java.util.Collection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.amse.bomberman.protocol.responses.ResponseCreator;
import org.amse.bomberman.server.gameservice.listeners.GameChangeListener;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.Controller;

/**
 * Class that represents lobby chat. And ingame kill info.
 * @author Kirilchuk V.E.
 */
public class AsynchroChat {
    private final Map<Variable, Constant> vars = new HashMap<Variable, Constant>();    

    public AsynchroChat() {
    }

    public void addMessage(String message, Collection<GameChangeListener> listeners) {
        for (GameChangeListener gameChangeListener : listeners) {
            gameChangeListener.newChatMessage(message);
            String answer = superMindAnswer(message);
            if(answer != null) {
                gameChangeListener.newChatMessage(message);
            }
        }
    }

    private String superMindAnswer(String text) {
        String        idName = null;
        StringBuilder res    = new StringBuilder();

        // Trying to find Identificator = Expression
        int    eqPos = text.indexOf('=');
        String buff;

        try {
            if (eqPos != -1) {
                String in = text.substring(0, eqPos);

                // Trying to get left side identificator name.
                idName = getIdentificatorName(in);

                // In the right side must be Expression.
                text = text.substring(eqPos + 1, text.length());
            }

            // Generating expression from text.
            Expression expression = ExprBuilder.generate(text);

            // Evaluating expression. Can throw Arithmetic exception or ParseException
            Double value = expression.evaluate(vars);

            buff = value.toString();

            if (idName != null) {

                // If it was Identificator = Expression then remembering value of identificator.
                vars.put(new Variable(idName), new Constant(value));

                // Adding identificator name in the beggining of result
                res.append(idName).append(" := ");
            } else {    // if it was just Expression

                // Adding text then equals and then value of expression.
                res.append(text).append(" = ");
            }

            res.append(buff);

            return ("SuperMind: " + res.toString());
        } catch (ArithmeticException ex) {
            System.err.println("Chat: addMessage warning. " +
                              "SuperMind got arithmetic exception."
                              + ex.getMessage());
        } catch (ParseException ex) {
            System.err.println("Chat: addMessage warning. " +
                              "SuperMind can`t parse expression."
                              + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("Chat: addMessage warning. " +
                              "SuperMind can`t parse expression."
                              + ex.getMessage());
        }

        return null;
    }

    private String getIdentificatorName(String input) throws ParseException {
        LexAnalyzer la = new LexAnalyzer(input);

        // Must be two lexems:
        // 1)Identificator 2)EOTEXT
        Lexema lex = la.nextLex();

        if (lex.type != Lexema.Type.IDENT) {
            throw new ParseException("Wrong Identificator in left side", 0);
        } else {
            String name = ((IdLexema) lex).name;

            lex = la.nextLex();

            if (lex != Lexema.EOTEXT) {
                throw new ParseException("Wrong Identificator in left side", 0);
            } else {
                return name;
            }
        }
    }
}
