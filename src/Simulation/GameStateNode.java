package Simulation;

import ADL.ADLAgent;
import ADL.ADLBaseAgent;
import ADL.ADLPlayer;
import ADL.ADLPlayerAction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Pakapon on 5/11/2017 AD.
 */
public class GameStateNode {

    private transient List<GameStateNode> prevStates;

    private List<ADLBaseAgent> baseAgents;

    private Map<ADLPlayerAction, GameStateNode> nextStates;

    private BigInteger totalSafePath = BigInteger.ONE;

    private BigInteger totalDangerousPath = BigInteger.ZERO;

    GameStateNode(List<ADLBaseAgent> baseAgents) {
        this.baseAgents = baseAgents;
        this.nextStates = new HashMap<>();
    }

    GameStateNode(GameStateNode prevState, List<ADLBaseAgent> baseAgents) {
        this.prevStates = new ArrayList<>();
        this.prevStates.add(prevState);
        this.baseAgents = baseAgents;
        this.nextStates = new HashMap<>();
        this.totalSafePath = prevState.totalSafePath;
        this.totalDangerousPath = prevState.totalDangerousPath;
    }

    void addNextState(ADLPlayerAction action, GameStateNode node){
        this.nextStates.put(action, node);
    }

    public ADLPlayer getPlayer() {
        return (ADLPlayer) this.baseAgents.stream()
                .filter(baseAgent -> baseAgent instanceof ADLPlayer)
                .findFirst()
                .get();
    }

    public List<ADLBaseAgent> getEnemyAgents() {
        return this.baseAgents.stream()
                .filter(ADLBaseAgent::isEnemy)
                .collect(Collectors.toList());
    }

    String toJSONString(){
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(this);
    }

    void updateAllADLAgents(){
        this.baseAgents
                .stream()
                .filter((baseAgent) -> baseAgent instanceof ADLAgent)
                .forEach((agent) -> ((ADLAgent) agent).update());
    }

    void spawnADLBaseAgents(){
        List<ADLBaseAgent> willSpawnAgents = GameManager.getInstance().getWillSpawnAgents();

        this.baseAgents.addAll(willSpawnAgents);
        willSpawnAgents
                .stream()
                .filter((baseAgent) -> baseAgent instanceof ADLAgent)
                .forEach((baseAgent -> ((ADLAgent) baseAgent).start()));
        willSpawnAgents.clear();
    }

    void destroyADLBaseAgents(){
        List<ADLBaseAgent> willDestroyAgents = GameManager.getInstance().getWillDestroyAgents();

        this.baseAgents.removeAll(willDestroyAgents);
        willDestroyAgents.clear();
    }

    void checkCollisions(){
        int size = this.baseAgents.size();
        for (int i = 0;i < size;i++)
        {
            ADLBaseAgent firstAgent = this.baseAgents.get(i);
            for (int j = i + 1;j < size;j++)
            {
                ADLBaseAgent secondAgent = this.baseAgents.get(j);
                if (firstAgent.isCollidedWith(secondAgent)) {
                    firstAgent.handleOnCollidedWith(secondAgent);

                    if ((firstAgent instanceof ADLPlayer && secondAgent.isEnemy() && secondAgent.isAttacker) ||
                            secondAgent instanceof ADLPlayer && firstAgent.isEnemy() && firstAgent.isAttacker) {
                        this.totalDangerousPath = this.totalDangerousPath.add(this.totalSafePath);
                        this.totalSafePath = BigInteger.ZERO;
                    }
                }
            }
        }
    }

    public boolean equals(GameStateNode node) {
        ADLPlayer thisPlayer = this.getPlayer();
        ADLPlayer nodePlayer = node.getPlayer();

        return !thisPlayer.isJumping() && !nodePlayer.isJumping() &&
                (Double.compare(thisPlayer.getX(), nodePlayer.getX()) == 0) &&
                thisPlayer.getPossibleMoves(this.getEnemyAgents()).equals(nodePlayer.getPossibleMoves(node.getEnemyAgents()));
    }

    public void merge(GameStateNode node) {
        GameStateNode previousNode = node.getPrevStates().get(0);

        previousNode.getNextStates().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == node)
                .findFirst()
                .ifPresent(entry -> previousNode.getNextStates().put(entry.getKey(), this));

        this.getPrevStates().add(previousNode);

        this.totalSafePath = this.totalSafePath.add(node.totalSafePath);
        this.totalDangerousPath = this.totalDangerousPath.add(node.totalDangerousPath);
    }

    //region Getter & Setter Methods
    public List<GameStateNode> getPrevStates() {
        return prevStates;
    }

    public Map<ADLPlayerAction, GameStateNode> getNextStates() {
        return nextStates;
    }

    public List<ADLBaseAgent> getBaseAgents() {
        return baseAgents;
    }

    public BigInteger getTotalSafePath() {
        return totalSafePath;
    }

    public BigInteger getTotalDangerousPath() {
        return totalDangerousPath;
    }
    //endregion

}
