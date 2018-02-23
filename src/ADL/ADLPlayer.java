package ADL;

import java.awt.geom.Arc2D;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Pakapon on 4/20/2017 AD.
 */
public class ADLPlayer extends ADLBaseAgent {

    private static final int MAX_VELOCITY_X = 5;
    private static final int MIN_VELOCITY_X = -5;
    private static final int HORIZONTAL_THRESHOLD = 2;

    public static ADLPlayer currentPlayer;

    private boolean isJumping;
    private boolean isFloating;

    private int gravityScale = 3;

    private Deque<ADLPlayerAction> actionButtonSequence = new ArrayDeque<>();
    private Set<ADLPlayerAction> selectedActionsWhileJumping = EnumSet.noneOf(ADLPlayerAction.class);

    public ADLPlayer(String name, double positionX, double positionY, double width, double height){
        super(name);

        this.x = positionX;
        this.y = positionY;
        this.width = width;
        this.height = height;

        this.group = "Player";
        this.isDefender = true;

        this.life = 100;

        this.isJumping = false;

        ADLPlayer.currentPlayer = this;
    }

    public void update(){

        final double GRAVITY_EFFECT = -9.81 * ADLAgent.FIXED_UPDATE_TIME * this.gravityScale;

        double newVelocityX = this.getVelocityX();
        double newVelocityY = this.getVelocityY();

        if (this.isFloating){
            newVelocityY = newVelocityY + GRAVITY_EFFECT;
        }

        this.setVelocity(newVelocityX, newVelocityY);
        this.setPosition(this.x + newVelocityX * ADLAgent.FIXED_UPDATE_TIME, this.y + newVelocityY * ADLAgent.FIXED_UPDATE_TIME);
    }

//    /**
//     * Simulate move
//     * @param nonPlayerAgents All Agents except player
//     */
//    public void move(List<ADLBaseAgent> nonPlayerAgents){
//        if (this.shouldJump(nonPlayerAgents)) {
//            this.jump();
//        }
//    }

    public void move(ADLPlayerAction action) {
        switch(action) {
            case Jump:
                this.jump();
                break;
            case MoveLeft:
                this.moveLeft();
                break;
            case MoveRight:
                this.moveRight();
                break;
            default:
                this.doNothing();
                break;
        }
    }

    /**
     * Get all Possible moves that a player can do by our heuristic if he/she should move
     * @param nonPlayerAgents All Agents except player
     * @return possibleMoves
     */
    public Set<ADLPlayerAction> getPossibleMoves(List<ADLBaseAgent> nonPlayerAgents) {
        Set<ADLPlayerAction> possibleMoves = EnumSet.noneOf(ADLPlayerAction.class);

        List<ADLBaseAgent> horizontallyDangerousAgents = this.getHorizontallyDangerousAgents(nonPlayerAgents);
        horizontallyDangerousAgents.stream().reduce((prev, curr) -> {
            if (this.getDistance(prev) > this.getDistance(curr)) {
                return curr;
            } else {
                return prev;
            }
        }).ifPresentOrElse(agent -> {
            if (this.isJumpable()) {
                if (!this.isCollidedWith(agent)) {
                    possibleMoves.add(ADLPlayerAction.Jump);
                } else {
                    possibleMoves.add(ADLPlayerAction.DoNothing);
                }
            }

            if (this.isJumping) {
                if (actionButtonSequence.isEmpty()) {
                    //Get the action that Player should commit to move toward then avoid colliding with nearest enemy
                    ADLPlayerAction action = agent.getVelocityX() >= 0 ? ADLPlayerAction.MoveLeft : ADLPlayerAction.MoveRight;
                    possibleMoves.add(ADLPlayerAction.DoNothing);
                    possibleMoves.add(action);
                } else {
                    possibleMoves.add(actionButtonSequence.getLast());
                    possibleMoves.add(ADLPlayerAction.DoNothing);
                }
            } else {
                if (!this.isCollidedWith(agent)) {
                    possibleMoves.add(ADLPlayerAction.DoNothing);
                }
            }
        }, () -> {
            if (!this.isJumping) {
                int deviation = Double.compare(this.getX(), -6.4000);
                if (deviation > 0) {
                    possibleMoves.add(ADLPlayerAction.MoveLeft);
                } else if (deviation < 0) {
                    possibleMoves.add(ADLPlayerAction.MoveRight);
                } else {
                    possibleMoves.add(ADLPlayerAction.DoNothing);
                }
            } else {
                possibleMoves.add(ADLPlayerAction.DoNothing);
            }
        });

        return possibleMoves;
    }

    /**
     *
     * @param nonPlayerAgents
     * @return
     */
    public List<ADLBaseAgent> getHorizontallyDangerousAgents(List<ADLBaseAgent> nonPlayerAgents){
        //TODO Create methods for each lambda expression
        return nonPlayerAgents.stream()
                .filter(agent -> agent.isAttacker && agent.isEnemy())
                .filter(agent -> !agent.isMovingAwayFrom(this))
                .filter(agent -> this.getDistance(agent) < HORIZONTAL_THRESHOLD)
                .filter(agent -> this.isJumping ? this.getBottomMostY() > agent.getTopMostY() || !(this.getTopMostY() < agent.getBottomMostY() || this.getBottomMostY() > agent.getTopMostY()) : !(this.getTopMostY() < agent.getBottomMostY() || this.getBottomMostY() > agent.getTopMostY()))
                .filter(agent -> Double.compare(agent.getVelocityX(), 0.000000) != 0)
                .collect(Collectors.toList());
    }

    private void jump(){
        if (this.isJumpable()){
            this.isFloating = true;
            this.isJumping = true;
            this.setVelocity(this.getVelocityX(), 11);
        }
    }

    private void moveLeft(){
        if (isJumping) {
            actionButtonSequence.add(ADLPlayerAction.MoveLeft);
        }
        this.setVelocity(MIN_VELOCITY_X, this.getVelocityY());
    }

    private void moveRight(){
        if (isJumping) {
            actionButtonSequence.add(ADLPlayerAction.MoveRight);
        }
        this.setVelocity(MAX_VELOCITY_X, this.getVelocityY());
    }

    private void doNothing(){
        if (isJumping) {
            actionButtonSequence.add(ADLPlayerAction.DoNothing);
        }
        this.setVelocity(0, this.getVelocityY());
    }

    @Override
    protected void moveAgentToBound(ADLBaseAgent baseAgent) {
        super.moveAgentToBound(baseAgent);

        if (baseAgent instanceof ADLEnvironment && ((ADLEnvironment) baseAgent).isHorizontalAlignment()){
            this.isFloating = false;
            this.isJumping = false;
            this.actionButtonSequence.clear();
            this.setVelocity(this.getVelocityX(), 0);
        }
    }

    private boolean isJumpable() {
        return !this.isFloating && !this.isJumping;
    }

    @Override
    public ADLPlayer clone() throws CloneNotSupportedException {
        ADLPlayer clone = (ADLPlayer) super.clone();

        clone.actionButtonSequence = new ArrayDeque<>();
        clone.actionButtonSequence.addAll(this.actionButtonSequence);

        clone.selectedActionsWhileJumping = EnumSet.noneOf(ADLPlayerAction.class);
        clone.selectedActionsWhileJumping.addAll(this.selectedActionsWhileJumping);

        return clone;
    }

    public boolean isJumping() {
        return isJumping;
    }
}
