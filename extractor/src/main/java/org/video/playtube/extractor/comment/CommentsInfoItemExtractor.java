package org.video.playtube.extractor.comment;

import org.video.playtube.extractor.InfoItemExtractor;
import org.video.playtube.extractor.exception.ParsingException;

public interface CommentsInfoItemExtractor extends InfoItemExtractor {
    String getCommentId() throws ParsingException;
    String getCommentText() throws ParsingException;
    String getAuthorName() throws ParsingException;
    String getAuthorThumbnail() throws ParsingException;
    String getAuthorEndpoint() throws ParsingException;
    String getPublishedTime() throws ParsingException;
    Integer getLikeCount() throws ParsingException;
}
