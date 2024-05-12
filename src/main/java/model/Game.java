package model;
import java.util.Date;
public class Game {
    private String gameID;
    private Date date;
    private String white;
    private int whiteElo;
    private String black;
    private int blackElo;
    private int totalMoves;
    private String opening;
    private String timeControl;
    private String termination;
    private String result;

    public Game(String gameID, Date date, String white, int whiteElo, String black, int blackElo, int totalMoves, String opening, String timeControl, String termination, String result) {
        this.gameID = gameID;
        this.date = date;
        this.white = white;
        this.whiteElo = whiteElo;
        this.black = black;
        this.blackElo = blackElo;
        this.totalMoves = totalMoves;
        this.opening = opening;
        this.timeControl = timeControl;
        this.termination = termination;
        this.result = result;
    }

    // toString method to display the game details
    @Override
    public String toString() {
        return "Game{" +
                "gameID='" + gameID + '\'' +
                ", date=" + date +
                ", white='" + white + '\'' +
                ", whiteElo=" + whiteElo +
                ", black='" + black + '\'' +
                ", blackElo=" + blackElo +
                ", totalMoves=" + totalMoves +
                ", opening='" + opening + '\'' +
                ", timeControl='" + timeControl + '\'' +
                ", termination='" + termination + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
