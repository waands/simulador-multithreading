import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Random;

// Classe que representa uma tarefa
class Tarefa implements Runnable {
    private String nome;
    private int tempo;
    private JTextArea textArea;
    private JPanel beforePanel;
    private JPanel afterPanel;
    private Color color;
    private Metrics metrics;

    public Tarefa(String nome, int tempo, JTextArea textArea, JPanel beforePanel, JPanel afterPanel, Color color,
            Metrics metrics) {
        this.nome = nome;
        this.tempo = tempo;
        this.textArea = textArea;
        this.beforePanel = beforePanel;
        this.afterPanel = afterPanel;
        this.color = color;
        this.metrics = metrics;
    }

    @Override
    public void run() {
        // Adiciona uma caixa colorida ao painel "before" e atualiza o textArea
        SwingUtilities.invokeLater(() -> {
            textArea.append("Tarefa " + nome + " iniciada\n");
            JPanel taskPanel = new JPanel(new BorderLayout());
            taskPanel.setPreferredSize(new Dimension(70, 70));
            taskPanel.setBorder(BorderFactory.createLineBorder(color, 5));
            JLabel label = new JLabel(nome, SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            taskPanel.add(label, BorderLayout.CENTER);
            taskPanel.setName(nome);
            beforePanel.add(taskPanel);
            beforePanel.revalidate();
            beforePanel.repaint();
        });
        try {
            // Simula o tempo de execução da tarefa com uma variação aleatória
            Random random = new Random();
            int randomDelay = random.nextInt(2000); // Adiciona um atraso aleatório de até 2 segundos
            metrics.incrementCycles(tempo + randomDelay / 1000); // Atualiza os ciclos totais
            Thread.sleep(tempo * 1000 + randomDelay);
            metrics.incrementExecutedInstructions(1); // Incrementa as instruções executadas
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Atualiza a interface após a conclusão da tarefa
        SwingUtilities.invokeLater(() -> {
            textArea.append("Tarefa " + nome + " concluída\n");
            JPanel taskPanel = new JPanel(new BorderLayout());
            taskPanel.setPreferredSize(new Dimension(70, 70));
            taskPanel.setBorder(BorderFactory.createLineBorder(color, 5));
            JLabel label = new JLabel(nome, SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            taskPanel.add(label, BorderLayout.CENTER);
            taskPanel.setName(nome);
            afterPanel.add(taskPanel);
            afterPanel.revalidate();
            afterPanel.repaint();
        });
    }
}

// Classe principal do simulador
public class MultithreadingSimulator extends JFrame {
    private JTextArea textArea;
    private JButton smtButton;
    private JButton imtButton;
    private JButton bmtButton;
    private JButton clearButton;
    private JPanel beforePanel;
    private JPanel afterPanel;
    private Color[] colors = { Color.RED, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CYAN };
    private JTextField taskCountField;
    private JLabel totalCyclesLabel;
    private JLabel ipcLabel;
    private JLabel bubbleCyclesLabel;

    private JPanel metricsPanel;

    public MultithreadingSimulator() {
        // Configurações da janela principal
        setTitle("Multithreading Simulator");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Área de texto para exibir mensagens
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(200, 600));
        add(scrollPane, BorderLayout.WEST);

        // Painel para exibir as caixas das tarefas antes e depois
        beforePanel = new JPanel(new FlowLayout());
        afterPanel = new JPanel(new FlowLayout());

        // Contêiner para os painéis de tarefas
        JPanel taskPanelContainer = new JPanel(new GridLayout(1, 2));
        taskPanelContainer.add(new JScrollPane(beforePanel));
        taskPanelContainer.add(new JScrollPane(afterPanel));
        add(taskPanelContainer, BorderLayout.CENTER);

        // Labels "Inicialização" e "Concluído"
        JPanel labelPanel = new JPanel(new GridLayout(1, 2));
        labelPanel.add(new JLabel("Inicialização", SwingConstants.CENTER));
        labelPanel.add(new JLabel("Concluído", SwingConstants.CENTER));
        add(labelPanel, BorderLayout.NORTH);

        // Painel de métricas
        metricsPanel = new JPanel(new FlowLayout());
        metricsPanel.setPreferredSize(new Dimension(800, 50));
        
        totalCyclesLabel = new JLabel("Total Cycles: 0");
        ipcLabel = new JLabel("IPC: 0.0");
        bubbleCyclesLabel = new JLabel("Bubble Cycles: 0");
        metricsPanel.add(totalCyclesLabel);
        metricsPanel.add(ipcLabel);
        metricsPanel.add(bubbleCyclesLabel);
        add(metricsPanel, BorderLayout.SOUTH);

        // Painel de botões para iniciar simulações e campo de entrada para número de
        // tarefas
        JPanel controlPanel = new JPanel();
        smtButton = new JButton("Start SMT Simulation");
        imtButton = new JButton("Start IMT Simulation");
        bmtButton = new JButton("Start BMT Simulation");
        clearButton = new JButton("Clear Text Area");
        taskCountField = new JTextField("5", 5);
        controlPanel.add(new JLabel("Number of Tasks:"));
        controlPanel.add(taskCountField);
        controlPanel.add(smtButton);
        controlPanel.add(imtButton);
        controlPanel.add(bmtButton);
        controlPanel.add(clearButton);
        add(controlPanel, BorderLayout.NORTH);

        // Ações dos botões
        smtButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetTaskPanels();
                textArea.append("Simulação SMT:\n");
                new Thread(() -> smtSimulator()).start();
            }
        });

        imtButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetTaskPanels();
                textArea.append("Simulação IMT:\n");
                new Thread(() -> imtSimulator()).start();
            }
        });

        bmtButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetTaskPanels();
                textArea.append("Simulação BMT:\n");
                new Thread(() -> bmtSimulator()).start();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
                totalCyclesLabel.setText("Total Cycles: 0");
                ipcLabel.setText("IPC: 0.0");
                bubbleCyclesLabel.setText("Bubble Cycles: 0");
            }
        });
    }

    // Reset the task panels
    private void resetTaskPanels() {
        beforePanel.removeAll();
        afterPanel.removeAll();
        beforePanel.revalidate();
        beforePanel.repaint();
        afterPanel.revalidate();
        afterPanel.repaint();
    }

    // Atualiza os rótulos de métricas na interface gráfica
    private void updateMetricsLabels(Metrics metrics) {
        totalCyclesLabel.setText("Total Cycles: " + metrics.getTotalCycles());
        ipcLabel.setText("IPC: " + String.format("%.2f", metrics.getIPC()));
        bubbleCyclesLabel.setText("Bubble Cycles: " + metrics.getBubbleCycles());
    }

    // Simulação para SMT (Simultaneous Multithreading)
    public void smtSimulator() {
        int numTasks = Integer.parseInt(taskCountField.getText());
        ExecutorService executor = Executors.newFixedThreadPool(numTasks);
        Metrics metrics = new Metrics();
        for (int i = 0; i < numTasks; i++) {
            executor.execute(
                    new Tarefa("SMT-" + i, 1, textArea, beforePanel, afterPanel, colors[i % colors.length], metrics));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> updateMetricsLabels(metrics));
    }

    // Simulação para IMT (Implicit Multithreading)
    public void imtSimulator() {
        int numTasks = Integer.parseInt(taskCountField.getText());
        Metrics metrics = new Metrics();
        Thread[] threads = new Thread[numTasks];
        for (int i = 0; i < numTasks; i++) {
            threads[i] = new Thread(
                    new Tarefa("IMT-" + i, 1, textArea, beforePanel, afterPanel, colors[i % colors.length], metrics));
            threads[i].start();
        }
        for (int i = 0; i < numTasks; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        SwingUtilities.invokeLater(() -> updateMetricsLabels(metrics));
    }

    // Simulação para BMT (Block Multithreading)
    public void bmtSimulator() {
        int numTasks = Integer.parseInt(taskCountField.getText());
        Metrics metrics = new Metrics();
        Thread[] threads = new Thread[numTasks];
        for (int i = 0; i < numTasks; i++) {
            threads[i] = new Thread(
                    new Tarefa("BMT-" + i, 1, textArea, beforePanel, afterPanel, colors[i % colors.length], metrics));
        }
        for (int i = 0; i < numTasks; i++) {
            threads[i].start();
        }
        for (int i = 0; i < numTasks; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        SwingUtilities.invokeLater(() -> updateMetricsLabels(metrics));
    }

    public static void main(String[] args) {
        // Cria e exibe a interface gráfica
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MultithreadingSimulator().setVisible(true);
            }
        });
    }
}

class Metrics {
    private int totalCycles;
    private int executedInstructions;
    private int bubbleCycles;

    public Metrics() {
        this.totalCycles = 0;
        this.executedInstructions = 0;
        this.bubbleCycles = 0;
    }

    public void incrementCycles(int cycles) {
        this.totalCycles += cycles;
    }

    public void incrementExecutedInstructions(int instructions) {
        this.executedInstructions += instructions;
    }

    public void incrementBubbleCycles(int cycles) {
        this.bubbleCycles += cycles;
    }

    public int getTotalCycles() {
        return totalCycles;
    }

    public int getExecutedInstructions() {
        return executedInstructions;
    }

    public int getBubbleCycles() {
        return bubbleCycles;
    }

    public double getIPC() {
        return executedInstructions / (double) totalCycles;
    }

    public void printMetrics() {
        System.out.println("Total Cycles: " + totalCycles);
        System.out.println("Executed Instructions: " + executedInstructions);
        System.out.println("Bubble Cycles: " + bubbleCycles);
        System.out.println("IPC: " + getIPC());
    }
}
