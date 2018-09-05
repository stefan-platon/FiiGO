package implementation;

public class TableRow {
    static String position;
    static boolean groupAttached;
    static int libertiesNumber;
    static int groupLibertiesNumber;
    static float netScore;
    static boolean prediction;

    private String positionObj;
    private boolean groupAttachedObj;
    private int libertiesNumberObj;
    private int groupLibertiesNumberObj;
    private float netScoreObj;
    private boolean predictionObj;

    TableRow(String positionObj, boolean groupAttachedObj, int libertiesNumberObj, int groupLibertiesNumberObj,
                    float netScoreObj, boolean predictionObj) {
        this.positionObj = positionObj;
        this.groupAttachedObj = groupAttachedObj;
        this.libertiesNumberObj = libertiesNumberObj;
        this.groupLibertiesNumberObj = groupLibertiesNumberObj;
        this.netScoreObj = netScoreObj;
        this.predictionObj = predictionObj;
    }

    public String getPositionObj() {
        return positionObj;
    }

    public boolean getGroupAttachedObj() {
        return groupAttachedObj;
    }

    public int getLibertiesNumberObj() {
        return libertiesNumberObj;
    }

    public int getGroupLibertiesNumberObj() {
        return groupLibertiesNumberObj;
    }

    public float getNetScoreObj() {
        return netScoreObj;
    }

    public boolean getPredictionObj() {
        return predictionObj;
    }
}
