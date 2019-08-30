package org.video.playtube.extractor.comment;

import java.io.IOException;

import org.video.playtube.extractor.ListExtractor.InfoItemsPage;
import org.video.playtube.extractor.ListInfo;
import org.video.playtube.extractor.PlayTube;
import org.video.playtube.extractor.StreamingService;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.linkhandler.ListLinkHandler;
import org.video.playtube.extractor.util.ExtractorHelper;

public class CommentsInfo extends ListInfo<CommentsInfoItem>{

	private CommentsInfo(int serviceId, ListLinkHandler listUrlIdHandler, String name) {
		super(serviceId, listUrlIdHandler, name);
		// TODO Auto-generated constructor stub
	}
	
	public static CommentsInfo getInfo(String url) throws IOException, ExtractionException {
        return getInfo(PlayTube.getServiceByUrl(url), url);
    }

	public static CommentsInfo getInfo(StreamingService serviceByUrl, String url) throws ExtractionException, IOException {
	    return getInfo(serviceByUrl.getCommentsExtractor(url));
	}

    private static CommentsInfo getInfo(CommentsExtractor commentsExtractor) throws IOException, ExtractionException {
        // for service which do not have a comment extractor
        if (null == commentsExtractor) {
            return null;
        }

        commentsExtractor.fetchPage();
        String name = commentsExtractor.getName();
        int serviceId = commentsExtractor.getServiceId();
        ListLinkHandler listUrlIdHandler = commentsExtractor.getLinkHandler();
        CommentsInfo commentsInfo = new CommentsInfo(serviceId, listUrlIdHandler, name);
        commentsInfo.setCommentsExtractor(commentsExtractor);
        InfoItemsPage<CommentsInfoItem> initialCommentsPage = ExtractorHelper.getItemsPageOrLogError(commentsInfo, commentsExtractor);
        commentsInfo.setRelatedItems(initialCommentsPage.getItems());
        commentsInfo.setNextPageUrl(initialCommentsPage.getNextPageUrl());
        return commentsInfo;
    }
    
    public static InfoItemsPage<CommentsInfoItem> getMoreItems(CommentsInfo commentsInfo, String pageUrl) throws ExtractionException, IOException {
        return getMoreItems(PlayTube.getService(commentsInfo.getServiceId()), commentsInfo, pageUrl);
    }
    
    public static InfoItemsPage<CommentsInfoItem> getMoreItems(StreamingService service, CommentsInfo commentsInfo, String pageUrl) throws IOException, ExtractionException {
        if (null == commentsInfo.getCommentsExtractor()) {
            commentsInfo.setCommentsExtractor(service.getCommentsExtractor(commentsInfo.getUrl()));
            commentsInfo.getCommentsExtractor().fetchPage();
        }
        return commentsInfo.getCommentsExtractor().getPage(pageUrl);
    }
    
    private transient CommentsExtractor commentsExtractor;

    public CommentsExtractor getCommentsExtractor() {
        return commentsExtractor;
    }

    public void setCommentsExtractor(CommentsExtractor commentsExtractor) {
        this.commentsExtractor = commentsExtractor;
    }

}
