package com.mta.playtube.extractor.comments;

import com.mta.playtube.extractor.ListExtractor;
import com.mta.playtube.extractor.ListInfo;
import com.mta.playtube.extractor.NewPipe;
import com.mta.playtube.extractor.StreamingService;
import com.mta.playtube.extractor.exceptions.ExtractionException;
import com.mta.playtube.extractor.linkhandler.ListLinkHandler;
import com.mta.playtube.extractor.utils.ExtractorHelper;

import java.io.IOException;

public class CommentsInfo extends ListInfo<CommentsInfoItem> {

	private CommentsInfo(int serviceId, ListLinkHandler listUrlIdHandler, String name) {
		super(serviceId, listUrlIdHandler, name);
		// TODO Auto-generated constructor stub
	}
	
	public static CommentsInfo getInfo(String url) throws IOException, ExtractionException {
        return getInfo(NewPipe.getServiceByUrl(url), url);
    }

	public static CommentsInfo getInfo(StreamingService serviceByUrl, String url) throws ExtractionException, IOException {
	    return getInfo(serviceByUrl.getCommentsExtractor(url));
	}

    private static CommentsInfo getInfo(CommentsExtractor commentsExtractor) throws IOException, ExtractionException {
        // for services which do not have a comments extractor
        if (null == commentsExtractor) {
            return null;
        }

        commentsExtractor.fetchPage();
        String name = commentsExtractor.getName();
        int serviceId = commentsExtractor.getServiceId();
        ListLinkHandler listUrlIdHandler = commentsExtractor.getLinkHandler();
        CommentsInfo commentsInfo = new CommentsInfo(serviceId, listUrlIdHandler, name);
        commentsInfo.setCommentsExtractor(commentsExtractor);
        ListExtractor.InfoItemsPage<CommentsInfoItem> initialCommentsPage = ExtractorHelper.getItemsPageOrLogError(commentsInfo, commentsExtractor);
        commentsInfo.setRelatedItems(initialCommentsPage.getItems());
        commentsInfo.setNextPageUrl(initialCommentsPage.getNextPageUrl());
        return commentsInfo;
    }
    
    public static ListExtractor.InfoItemsPage<CommentsInfoItem> getMoreItems(CommentsInfo commentsInfo, String pageUrl) throws ExtractionException, IOException {
        return getMoreItems(NewPipe.getService(commentsInfo.getServiceId()), commentsInfo, pageUrl);
    }
    
    public static ListExtractor.InfoItemsPage<CommentsInfoItem> getMoreItems(StreamingService service, CommentsInfo commentsInfo, String pageUrl) throws IOException, ExtractionException {
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
