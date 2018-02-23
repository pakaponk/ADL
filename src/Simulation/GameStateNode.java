package Simulation;

import ADL.ADLAgent;
import ADL.ADLBaseAgent;
import ADL.ADLPlayer;
import ADL.ADLPlayerAction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Pakapon on 5/11/2017 AD.
 */
public class GameStateNode {

    private transient GameStateNode prevState;

    private List<ADLBaseAgent> baseAgents;

    private Map<ADLPlayerAction, GameStateNode> nextStates;

    private boolean isDangerousPath;

    GameStateNode(List<ADLBaseAgent> baseAgents) {
        this.baseAgents = baseAgents;
        this.nextStates = new HashMap<>();
    }

    GameStateNode(GameStateNode prevState, List<ADLBaseAgent> baseAgents) {
        this.prevState = prevState;
        this.baseAgents = baseAgents;
        this.nextStates = new HashMap<>();
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

                    if (firstAgent instanceof ADLPlayer) {
                        this.isDangerousPath = true;
                    } else if (secondAgent instanceof ADLPlayer) {
                        this.isDangerousPath = true;
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

    //region Getter & Setter Methods
    public GameStateNode getPrevState() {
        return prevState;
    }

    public Map<ADLPlayerAction, GameStateNode> getNextStates() {
        return nextStates;
    }

    public List<ADLBaseAgent> getBaseAgents() {
        return baseAgents;
    }
    //endregion

}
