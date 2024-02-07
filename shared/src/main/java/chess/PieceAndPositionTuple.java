package chess;

import java.util.Objects;

public class PieceAndPositionTuple<Piece, Position>{
    public final Piece piece;
    public final Position position;

    public PieceAndPositionTuple(Piece piece, Position position) {
        this.piece = piece;
        this.position = position;
    }

    public Piece getPiece() { return this.piece; }

    public Position getPosition() { return this.position; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PieceAndPositionTuple<?, ?> that = (PieceAndPositionTuple<?, ?>) o;
        return Objects.equals(piece, that.piece) && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, position);
    }

    @Override
    public String toString() {
        return "PieceAndPositionTuple{" +
                "piece=" + piece +
                ", position=" + position +
                '}';
    }
}