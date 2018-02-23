package Simulation;

import ADL.*;
import ADL.Actions.ADLAction;
import ADL.Actions.ADLMoveAction;


import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created by Pakapon on 3/3/2017 AD.
 */
public class Main {

    public static void main(String[] args){
        try {
            Main.testGameStateNode();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void testGameStateNode() throws Exception {

        ADLAgent iceMan = ADLAgentFactory.getInstance().createADLAgent("src/IceMan.txt");
        ADLPlayer player = new ADLPlayer("Player", -6.4, -4.42, 0.64, 0.64);
        ADLEnvironment wallLeft = new ADLEnvironment("WallLeft", -9.28, 0, 0.64, 10.8, false);
        ADLEnvironment wallRight = new ADLEnvironment("WallRight", 9.28, 0, 0.64, 10.8, false);
        ADLEnvironment floor = new ADLEnvironment("Floor", 0, -5.08, 19.2, 0.64, false, true);

        GameManager gameManager = GameManager.getInstance();
        gameManager.getBaseAgents().add(wallLeft);
        gameManager.getBaseAgents().add(wallRight);
        gameManager.getBaseAgents().add(floor);
        gameManager.getBaseAgents().add(iceMan);
        gameManager.getBaseAgents().add(player);

        gameManager.simulate();

        //gameManager.convertGameStateNodeToJSONFile("src/simulate.json");

        instantiateGameScreen(gameManager.getRootGameStateNode());

//        GameStateNode node = new GameStateNode(baseAgents);
//        GameStateNode node2 = new GameStateNode(baseAgents);
//        GameStateNode node3 = new GameStateNode(baseAgents);
//
//        node.addNextState(node2);
//        node.addNextState(node3);
//
//        String jsonString = node.toJSONString();
//
//        List<String> lines = Arrays.asList(jsonString);
//
//        Path file = Paths.get("src/gameState.json");
//        Files.write(file, lines, Charset.forName("UTF-8"));

//        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
//        GameStateNode newNodeFromJson = gson.fromJson(jsonString, GameStateNode.class);
    }

    private static void instantiateGameScreen(GameStateNode rootNode){
        JFrame frame = new JFrame();

        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 540));
        GameScreen gameScreen = new GameScreen(rootNode, textArea);

        frame.setLayout(new BorderLayout());
        frame.add(gameScreen, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.EAST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);

        frame.pack();
        frame.setVisible(true);

        gameScreen.requestFocus();
    }

    private static void doSomething(ADLAgent agent) throws Exception {
        ADLScript script = agent.getScript();

        ADLSequence seq = script.getStates().get("state1").getSequences().get(0);

        System.out.println("Agent Initial X: " + agent.getScript().getAgentInitialPositionX());
        System.out.println("Agent Initial Y: " + agent.getScript().getAgentInitialPositionY());

        ArrayList<Entity> agents = new ArrayList<>();
        ADLPlayer playerAgent = new ADLPlayer("Player", -6.39, -4.42, 0.64, 0.64);
        ADLEnvironment wallRight = new ADLEnvironment("WallRight", 9.28, 5.48, 20.48, 0.64, false);
        ADLEnvironment wallLeft = new ADLEnvironment("WallLeft", -9.28, 5.48, 20.48, 0.64, false);

        Timeline timeline = new Timeline();
        for (ADLAction action: seq.actions)
        {
            if (action instanceof ADLMoveAction)
            {
                System.out.println(((ADLMoveAction) action).getXEquationString());
                System.out.println(((ADLMoveAction) action).getYEquationString());
            }
        }
    }
}
