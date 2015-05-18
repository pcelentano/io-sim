package model.simplex;

import org.codehaus.jackson.JsonNode;
import play.mvc.WebSocket;

/**
 * Created by Mart0
 * Date: 6/19/12
 */

public class User {
    private String id;
    private WebSocket.Out<JsonNode> channel;
    private Solver solver;


    public User(String id, WebSocket.Out<JsonNode> out) {
        this.id = id;
        channel = out;
        solver = new Solver();
    }

    public WebSocket.Out<JsonNode> getChannel() {
        return channel;
    }

    public String getId() {
        return id;
    }


    public void initSolver(String[][] tableau, String mValue, String objective) {
        solver.init(tableau, mValue, objective);
    }

    public int getCurrentIteration() {
        return solver.getCurrentIteration();
    }

    public int getOptimalIteration() {
        return solver.getOptimalIteration();
    }

    public String[][] getCurrentTableau() {
        return solver.getCurrentTableau();
    }

    public void doNextTableau() {
        solver.iterate();
    }

    public void doPreviousTableau() {
        solver.unIterate();
    }

    public void doOptimalTableau() {
        solver.goToOptimal();
    }

    public void doInitialTableau() {
        solver.goToInitial();

    }

    public String getOptimalIndex() {
        return solver.getPivot();
    }

    public boolean isAlternativeSolution() {
        return solver.isAlternativeSolution();
    }

    public String getThetaTie() {
        return solver.getThetaTie();
    }

    public boolean isOpenPolyhedron() {
        return solver.getOpenPolyhedronList();
    }

    public boolean isIncompatibleSolution() {
        return solver.isIncompatibleSolution();
    }

}
