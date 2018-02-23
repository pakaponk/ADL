package Simulation;

import ADL.ADLAgent;
import ADL.ADLBaseAgent;
import ADL.ADLPlayer;
import ADL.ADLPlayerAction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class GameManager {

    private GameStateNode rootGameStateNode;

    private List<ADLBaseAgent> baseAgents = new ArrayList<>();
    private List<ADLBaseAgent> willSpawnAgents = new ArrayList<>();
    private List<ADLBaseAgent> willDestroyAgents = new ArrayList<>();

    private static GameManager instance = new GameManager();

    public static GameManager getInstance(){
        return GameManager.instance;
    }

    private GameManager() {
        super();
    }

//    private GameManager(List<ADLBaseAgent> baseAgents){
//        super();
//        this.baseAgents = baseAgents;
//        this.player = (ADLPlayer) baseAgents.stream().filter(baseAgent -> baseAgent instanceof ADLPlayer).findFirst().get();
//    }

    //region Getter
    public GameStateNode getRootGameStateNode() {
        return rootGameStateNode;
    }

    public List<ADLBaseAgent> getBaseAgents() {
        return baseAgents;
    }

    public List<ADLBaseAgent> getWillSpawnAgents() {
        return willSpawnAgents;
    }
    public List<ADLBaseAgent> getWillDestroyAgents() {
        return willDestroyAgents;
    }

    public ADLPlayer getPlayer() {
        return (ADLPlayer) baseAgents.stream().filter(baseAgent -> baseAgent instanceof ADLPlayer).findFirst().get();
    }
    //endregion

    private GameStateNode createGameStateNode(){

        return new GameStateNode(this.cloneAgents());
    }

    private GameStateNode createGameStateNode(GameStateNode prevState) {

        return new GameStateNode(prevState, this.cloneAgents());
    }

    private List<ADLBaseAgent> cloneAgents() {
        return this.baseAgents.stream().map(baseAgent -> {
            try {
                return baseAgent.clone();
            } catch (CloneNotSupportedException e) {
                System.err.println("Unable to clone an instance of class " + baseAgent.getClass().getName());
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }

    public void simulate(){

        this.startAllADLAgents();

        this.rootGameStateNode = this.createGameStateNode();

        int frequency = 2;
        int totalRound = 600 / frequency;

        List<GameStateNode> updatingStateNodes = new ArrayList<>();
        List<GameStateNode> nextUpdatingStateNodes = new ArrayList<>();
        nextUpdatingStateNodes.add(this.rootGameStateNode);

        long begin = System.currentTimeMillis();

        for (int i = 1;i <= totalRound;i++) {

            updatingStateNodes.clear();
            updatingStateNodes.addAll(nextUpdatingStateNodes);
            nextUpdatingStateNodes.clear();

            System.out.println("-------- Round " + i + " START --------");

            updatingStateNodes.forEach(node -> {
                this.baseAgents = node.getBaseAgents();

                Set<ADLPlayerAction> possibleMoves = node.getPlayer().getPossibleMoves(node.getEnemyAgents());
                //possibleMoves.forEach(action -> System.out.print(action.name() + " "));
                //System.out.println();

                List<GameStateNode> nextGameStateNodes = possibleMoves.stream().map(action -> {
                    //System.out.println("**************");
                    GameStateNode nextNode = this.createGameStateNode(node);

                    ADLPlayer player = nextNode.getPlayer();
                    player.move(action);

                    for (int j = 0; j < frequency;j++) {
                        nextNode.checkCollisions();
                        nextNode.updateAllADLAgents();
                        player.update();
                        nextNode.spawnADLBaseAgents();
                        //nextNode.printCurrentPosition();
                        nextNode.destroyADLBaseAgents();
                    }

                    //System.out.println("**************");
                    node.addNextState(action, nextNode);
                    return nextNode;

                }).collect(Collectors.toList());

                nextUpdatingStateNodes.addAll(nextGameStateNodes);
            });

            System.out.println("Next Update Nodes size: " + nextUpdatingStateNodes.size());

            int totalIdenticalNodes = 0;
            boolean[] identicalNodes = new boolean[nextUpdatingStateNodes.size()];
            for (int j = 0;j < nextUpdatingStateNodes.size();j++) {
                GameStateNode firstNode = nextUpdatingStateNodes.get(j);
                for (int k = nextUpdatingStateNodes.size() - 1; k >= (j + 1);k--) {
                    if (identicalNodes[k]) {
                        break;
                    }

                    GameStateNode secondNode = nextUpdatingStateNodes.get(k);

                    if (firstNode.equals(secondNode)) {
                        //System.out.println("Identical Node: " + firstNode.toString() + " " + secondNode.toString());
                        identicalNodes[k] = true;
                        totalIdenticalNodes++;

                        firstNode.merge(secondNode);

                        nextUpdatingStateNodes.remove(k);
                    }
                }
            }

            System.out.println("Total Identical Nodes: " + totalIdenticalNodes);
            System.out.println("-------- Round " + i + " END ----------\n");
        }

        BigInteger totalSafePath = nextUpdatingStateNodes.stream().map(GameStateNode::getTotalSafePath).reduce(BigInteger::add).get();
        BigInteger totalDangerousPath = nextUpdatingStateNodes.stream().map(GameStateNode::getTotalDangerousPath).reduce(BigInteger::add).get();

        long end = System.currentTimeMillis();

        System.out.println("Total Safe Path: " + totalSafePath + ", Total Dangerous Path: " + totalDangerousPath);
        System.out.println("Ratio: " + new BigDecimal(totalSafePath).divide(new BigDecimal(totalDangerousPath), 10, RoundingMode.HALF_UP));

        System.out.println("Finished in: " + (end - begin) + " ms");
    }

    public void convertGameStateNodeToJSONFile(String directory) throws IOException {
        String jsonString = this.rootGameStateNode.toJSONString();

        List<String> lines = Arrays.asList(jsonString);

        Path file = Paths.get(directory);
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    private void startAllADLAgents(){
        this.baseAgents
                .stream()
                .filter((baseAgent) -> baseAgent instanceof ADLAgent)
                .forEach((agent) -> ((ADLAgent) agent).start());
    }

    private void printCurrentPosition(){
        this.baseAgents
                .forEach(baseAgent -> System.out.println(baseAgent.name + " - X: " + baseAgent.x + " ,Y: " + baseAgent.y));
    }
}
