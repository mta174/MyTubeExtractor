package com.mta.playtube.extractor.comments;

import com.mta.playtube.extractor.InfoItemExtractor;
import com.mta.playtube.extractor.exceptions.ParsingException;

public interface CommentsInfoItemExtractor extends InfoItemExtractor {
    String getCommentId() throws ParsingException;
    String getCommentText() throws ParsingException;
    String getAuthorName() throws ParsingException;
    String getAuthorThumbnail() throws ParsingException;
    String getAuthorEndpoint() throws ParsingException;
    String getPublishedTime() throws ParsingException;
    Integer getLikeCount() throws ParsingException;
}
