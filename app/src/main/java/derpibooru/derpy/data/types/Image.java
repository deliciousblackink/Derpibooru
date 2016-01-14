package derpibooru.derpy.data.types;

import java.util.Date;
import java.util.HashMap;

public class Image {
    private int mId;
    private int mScore;
    private int mUpvotes;
    private int mDownvotes;
    private int mFaves;
    private int mCommentCount;
    private String mImgUrl;
    /* additional fields */
    private Date mCreatedAt;
    private HashMap<Integer, String> mTags;
    private String mSourceUrl;
    private String mDescription;
    private String mUploader;

    public Image(int id, int score, int upvotes, int downvotes, int faves,
                 int comments, String imgUrl, Date createdAt,
                 HashMap<Integer, String> tags, String sourceUrl,
                 String description, String uploader) {
        mId = id;
        mScore = score;
        mUpvotes = upvotes;
        mDownvotes = downvotes;
        mFaves = faves;
        mCommentCount = comments;
        mImgUrl = "https:" + imgUrl;
        mCreatedAt = createdAt;
        mTags = tags;
        mSourceUrl = sourceUrl;
        mDescription = description;
        mUploader = uploader;
    }

    public int getId() {
        return mId;
    }

    public int getScore() {
        return mScore;
    }

    public int getUpvotes() {
        return mUpvotes;
    }

    public int getDownvotes() {
        return mDownvotes;
    }

    public int getFaves() {
        return mFaves;
    }

    public int getCommentCount() {
        return mCommentCount;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public HashMap<Integer, String> getTags() {
        return mTags;
    }

    public String getSourceUrl() {
        return mSourceUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getUploader() {
        return mUploader;
    }
}
