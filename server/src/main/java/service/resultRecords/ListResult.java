package service.resultRecords;

import chess.model.GameData;

import java.util.Collection;

public record ListResult(Collection<ListResultBody> games) {
}
