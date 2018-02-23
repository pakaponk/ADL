package Simulation;


import ADL.ADLBaseAgent;
import ADL.ADLPlayer;
import ADL.ADLPlayerAction;

import javax.swing.*;
import java.awt.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class GameScreen extends JPanel {

    private static final Font STATUS_FONT = new Font("Default", Font.BOLD, 32);

    private static final int WIDTH = 960;
    private static final int HEIGHT = 540;

    private static final double RATIO = 0.5;

    private GameStateNode rootNode;
    private GameStateNode currentNode;
    private JTextArea textArea;
    private int round = 1;

    GameScreen(GameStateNode rootNode, JTextArea textArea){
        super();

        this.textArea = textArea;
        this.rootNode = rootNode;
        this.currentNode = rootNode;

        this.initializeUI();
        this.initializeListener();
    }

    private void initializeUI(){
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setVisible(true);
    }

    private void initializeListener(){
        GameScreen _self = this;

        this.addKeyListener(new KeyListener() {

            Set<Integer> triggeredKeys = new HashSet<>();

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (!triggeredKeys.contains(e.getKeyCode())) {
                    switch (e.getKeyCode()){
                        case KeyEvent.VK_LEFT:
                            try{
                                _self.currentNode = _self.currentNode.getPrevStates().get(0);
                                _self.repaint();
                                _self.round--;
                            }
                            catch(Exception exception) {
                                System.out.println("Already at Root State");
                                _self.currentNode = _self.rootNode;
                                _self.repaint();
                                _self.round = 1;
                            }
                            break;
                        case KeyEvent.VK_RIGHT:
                            if (_self.currentNode.getNextStates().containsKey(ADLPlayerAction.DoNothing)){
                                _self.currentNode = _self.currentNode.getNextStates().get(ADLPlayerAction.DoNothing);
                                _self.repaint();
                                _self.round++;
                            }
                            else{
                                System.out.println("Do Nothing is not available");
                            }
                            break;
                        case KeyEvent.VK_A:
                            if (_self.currentNode.getNextStates().containsKey(ADLPlayerAction.MoveLeft)){
                                _self.currentNode = _self.currentNode.getNextStates().get(ADLPlayerAction.MoveLeft);
                                _self.repaint();
                                _self.round++;
                            }
                            else{
                                System.out.println("Move Left is not available");
                            }
                            break;
                        case KeyEvent.VK_D:
                            if (_self.currentNode.getNextStates().containsKey(ADLPlayerAction.MoveRight)){
                                _self.currentNode = _self.currentNode.getNextStates().get(ADLPlayerAction.MoveRight);
                                _self.repaint();
                                _self.round++;
                            }
                            else{
                                System.out.println("Move Right is not available");
                            }
                            break;
                        case KeyEvent.VK_J:
                            if (_self.currentNode.getNextStates().containsKey(ADLPlayerAction.Jump)){
                                _self.currentNode = _self.currentNode.getNextStates().get(ADLPlayerAction.Jump);
                                _self.repaint();
                                _self.round++;
                            }
                            else{
                                System.out.println("Jump is not available");
                            }
                            break;
                        case KeyEvent.VK_N:
                            int i;
                            for (i = 0;i < 10;i++) {
                                if (!_self.currentNode.getNextStates().containsKey(ADLPlayerAction.DoNothing)) {
                                    break;
                                }
                                _self.currentNode = _self.currentNode.getNextStates().get(ADLPlayerAction.DoNothing);
                            }
                            _self.repaint();
                            _self.round += i;
                            break;
                        case KeyEvent.VK_P:
                            try{
                                for (i = 0;i < 10;i++) {
                                    _self.currentNode = _self.currentNode.getPrevStates().get(0);
                                }
                                _self.repaint();
                                _self.round -= 10;
                            }
                            catch (Exception exception){
                                System.out.println("Already at Root State");
                                _self.currentNode = _self.rootNode;
                                _self.repaint();
                                _self.round = 1;
                            }
                            break;
                        default:
                            break;
                    }
                    triggeredKeys.add(e.getKeyCode());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                triggeredKeys.remove(e.getKeyCode());
            }
        });

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentNode.getBaseAgents()
                        .stream()
                        .filter(baseAgent -> {
                            int relativeX, relativeY, width, height, relativeWidth, relativeHeight;

                            width = (int)(baseAgent.width * 100);
                            height = (int)(baseAgent.height * 100);

                            relativeWidth = (int) (width * RATIO);
                            relativeHeight = (int) (height * RATIO);

                            relativeX = (int)(((baseAgent.x * 100) + WIDTH - width / 2) * RATIO);
                            relativeY = (int)(((-baseAgent.y * 100) + HEIGHT - height / 2) * RATIO);

                            return (e.getX() > relativeX &&
                                    e.getX() < (relativeX + relativeWidth) &&
                                    e.getY() > relativeY &&
                                    e.getY() < (relativeY + relativeHeight));
                        })
                        .findFirst()
                        .ifPresent(baseAgent -> textArea.setText(baseAgent.toString()));
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.clearRect(0, 0, 1920, 1080);

        this.drawBaseAgents(g2d);
        this.drawStatusText(g2d);
    }

    private void drawStatusText(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(STATUS_FONT);
        g2d.drawString("Round #" + this.round + " Total next Nodes: "  + this.currentNode.getNextStates().size() , 50, 50);

        this.currentNode.getNextStates()
                .keySet()
                .stream()
                .map(Enum::name)
                .reduce((String result, String current) -> result + " " + current)
                .ifPresent(text -> g2d.drawString(text, 50, 100));

        g2d.drawString("Total Safe Path: " + currentNode.getTotalSafePath(), 50, 150);
    }

    private void drawBaseAgents(Graphics2D g2d) {
        ADLPlayer player = this.currentNode.getPlayer();

        List<ADLBaseAgent> dangerousAgents = player.getHorizontallyDangerousAgents(this.currentNode.getEnemyAgents());

        this.currentNode.getBaseAgents().forEach(baseAgent -> {
            switch (baseAgent.group){
                case "Environment":
                    g2d.setColor(Color.LIGHT_GRAY);
                    break;
                case "Player":
                    g2d.setColor(Color.DARK_GRAY);
                    break;
                default:
                    if (dangerousAgents.contains(baseAgent))
                        g2d.setColor(Color.RED);
                    else
                        g2d.setColor(Color.PINK);
                    break;
            }

            int relativeX, relativeY, width, height, relativeWidth, relativeHeight;

            width = (int)(baseAgent.width * 100);
            height = (int)(baseAgent.height * 100);

            relativeWidth = (int) (width * RATIO);
            relativeHeight = (int) (height * RATIO);

            relativeX = (int)(((baseAgent.x * 100) + WIDTH - width / 2) * RATIO);
            relativeY = (int)(((-baseAgent.y * 100) + HEIGHT - height / 2) * RATIO);

            //Draw Body
            g2d.fillRect(relativeX, relativeY, relativeWidth, relativeHeight);

            if (!baseAgent.group.equals("Environment") && !baseAgent.isProjectile){
                g2d.setColor(Color.WHITE);

                int eyeWidth = (int) (relativeWidth * 0.2);
                int eyeHeight = (int) (relativeHeight * 0.2);

                if (baseAgent.horizontalDirection == 1){
                    g2d.fillRect( relativeX + (int)(relativeWidth * 0.6), relativeY + eyeHeight,
                            eyeWidth, eyeHeight);
                } else {
                    g2d.fillRect( relativeX + (int)(relativeWidth * 0.2), relativeY + eyeHeight,
                            eyeWidth, eyeHeight);
                }
            }
        });
    }
}
