package frc.robot;

import edu.wpi.first.wpilibj.Timer;

public class NodeSelector {
    private int currentRow = 0;
    private int currentColumn = 0;

    private final String[][] grid = {
        {"H1", "H2", "H3", "H4", "H5", "H6", "H7", "H8", "H9"},
        {"M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9"},
        {"L1", "L2", "L3", "L4", "L5", "L6", "L7", "L8", "L9"}
    };

    public void updateSelectedNode(int povValue) {
        if (povValue == 0) {
            currentRow = Math.max(currentRow - 1, 0);
        }
        else if (povValue == 180) {
            currentRow = Math.min(currentRow + 1, grid.length - 1);
        }
        else if (povValue == 90) {
            currentColumn = Math.min(currentColumn + 1, grid[0].length - 1);
        }
        else if (povValue == 270) {
            currentColumn = Math.max(currentColumn - 1, 0);
        }

        Timer.delay(0.15);
    }

    public String getCurrentNode() {
        return grid[currentRow][currentColumn];
    }
}
