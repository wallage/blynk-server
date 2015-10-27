package cc.blynk.server.model;

import cc.blynk.server.exceptions.IllegalCommandException;
import cc.blynk.server.model.graph.GraphKey;
import cc.blynk.server.model.widgets.Widget;
import cc.blynk.server.model.widgets.others.Timer;
import cc.blynk.server.model.widgets.outputs.Graph;
import cc.blynk.server.utils.JsonParser;

import java.util.*;


/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:04
 */
public class Profile {

    public final transient Set<GraphKey> graphPins;

    //todo remove in next release
    public volatile Integer activeDashId;

    public DashBoard[] dashBoards;

    public Profile() {
        this.graphPins = new HashSet<>();
        this.dashBoards = new DashBoard[0];
    }

    /**
     * Check if dashboardId is real and exists in user profile.
     */
    public void validateDashId(int dashBoardId, int msgId) {
        for (DashBoard dashBoard : dashBoards) {
            if (dashBoard.id == dashBoardId) {
                return;
            }
        }

        throw new IllegalCommandException(String.format("Requested token for non-existing '%d' dash id.", dashBoardId), msgId);
    }

    public int getDashIndex(int dashId, int msgId) {
        for (int i = 0; i < dashBoards.length; i++) {
            if (dashBoards[i].id == dashId) {
                return i;
            }
        }
        throw new IllegalCommandException("Dashboard with passed id not found.", msgId);
    }

    public DashBoard getDashById(int dashId, int msgId) {
        for (DashBoard dashBoard : dashBoards) {
            if (dashBoard.id == dashId) {
                return dashBoard;
            }
        }

        throw new IllegalCommandException(String.format("Requested token for non-existing '%d' dash id.", dashId), msgId);
    }

    public DashBoard getDashboardById(int id, int msgId) {
        for (DashBoard dashBoard : dashBoards) {
            if (dashBoard.id == id) {
                return dashBoard;
            }
        }
        throw new IllegalCommandException(String.format("Requested token for non-existing '%d' dash id.", id), msgId);
    }

    public List<Timer> getActiveTimerWidgets() {
        if (dashBoards.length == 0) {
            return Collections.emptyList();
        }

        List<Timer> activeTimers = new ArrayList<>();
        for (DashBoard dashBoard : dashBoards) {
            if (dashBoard.isActive) {
                activeTimers.addAll(dashBoard.getTimerWidgets());
            }
        }
        return activeTimers;
    }

    public void calcGraphPins() {
        if (dashBoards.length == 0) {
            return;
        }

        for (DashBoard dashBoard : dashBoards) {
            if (dashBoard.widgets == null || dashBoard.widgets.length == 0) {
                continue;
            }

            for (Widget widget : dashBoard.widgets) {
                if (widget instanceof Graph) {
                    if (widget.pin != null) {
                        graphPins.add(new GraphKey(dashBoard.id, widget.pin, widget.pinType));
                    }
                }
            }
        }
    }

    public boolean hasGraphPin(GraphKey key) {
        return graphPins != null && key != null && graphPins.contains(key);
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profile that = (Profile) o;

        if (!Arrays.equals(dashBoards, that.dashBoards)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(dashBoards);
    }
}
