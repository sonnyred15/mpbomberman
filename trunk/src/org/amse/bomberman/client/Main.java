package org.amse.bomberman.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.impl.ControllerImpl;
import org.amse.bomberman.client.control.impl.ModelsContainer;
import org.amse.bomberman.client.net.Connector;
import org.amse.bomberman.client.net.impl.ConnectorImpl;
import org.amse.bomberman.client.viewmanager.ViewManager;

/**
 *
 * @author Michail Korovkin
 * @author Kirilchuk V.E.
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (ClassNotFoundException ex) {
//            ex.printStackTrace();
//        } catch (InstantiationException ex) {
//            ex.printStackTrace();
//        } catch (IllegalAccessException ex) {
//            ex.printStackTrace();
//        } catch (UnsupportedLookAndFeelException ex) {
//            ex.printStackTrace();
//        }

        Connector connector = new ConnectorImpl();

        ModelsContainer context = new ModelsContainer();

        ExecutorService executors
                = Executors.newFixedThreadPool(2, new DaemonThreadFactory());

        Controller controller = new ControllerImpl(executors, connector, context);
        connector.setListener(controller);

        ViewManager viewState = new ViewManager(controller);
        context.getConnectionStateModel().addListener(viewState);
        viewState.showWizard();

        Runtime.getRuntime().addShutdownHook(new ShutdowHook(executors, controller));
    }

    private static class ShutdowHook extends Thread {

        private final ExecutorService executors;
        private final Controller controller;

        public ShutdowHook(ExecutorService executors, Controller controller) {
            this.executors = executors;
            this.controller = controller;
        }

        @Override
        public void run() {
            if (controller != null) {
                controller.disconnect();
            }
            executors.shutdown();
        }
    }

    private static class DaemonThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DaemonThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup()
                    : Thread.currentThread().getThreadGroup();
            namePrefix = "pool-"
                    + poolNumber.getAndIncrement()
                    + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (!t.isDaemon()) {
                t.setDaemon(true);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
