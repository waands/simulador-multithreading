import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Classe que representa uma tarefa
class Tarefa implements Runnable {
    private String nome;
    private int tempo;

    public Tarefa(String nome, int tempo) {
        this.nome = nome;
        this.tempo = tempo;
    }

    @Override
    public void run() {
        System.out.println("Tarefa " + nome + " iniciada");
        try {
            Thread.sleep(tempo * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Tarefa " + nome + " concluída");
    }
}

// Classe que implementa os simuladores
public class MultithreadingSimulator {

    // Simulador para SMT (Simultaneous Multithreading)
    public static void smtSimulator() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executor.execute(new Tarefa("SMT-" + i, 2000));
        }
        executor.shutdown();
    }

    // Simulador para IMT (Implicit Multithreading)
    public static void imtSimulator() {
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(new Tarefa("IMT-" + i, 2000));
            threads[i].start();
        }
        for (int i = 0; i < 5; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Simulador para BMT (Block Multithreading)
    public static void bmtSimulator() {
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(new Tarefa("BMT-" + i, 2000));
        }
        for (int i = 0; i < 5; i++) {
            threads[i].start();
        }
        for (int i = 0; i < 5; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Método main para testar os simuladores
    public static void main(String[] args) {
        System.out.println("Simulação SMT:");
        smtSimulator();

        System.out.println("\nSimulação IMT:");
        imtSimulator();

        System.out.println("\nSimulação BMT:");
        bmtSimulator();
    }
}
