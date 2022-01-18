package com.wifiview.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PathConfig
{
    public static SdcardSelector sdcardItem = SdcardSelector.BUILT_IN;

    public static final String PHOTOS_PATH = "/MoView/Photos";
    public static final String VIDEOS_PATH = "/MoView/Videos";
    private final static String PARENTFOLDER = "MoView";
    private final static String PHOTOS = "Photos";
    private final static String VIDEOS = "Videos";

    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;

    private static List<String> videoList = new ArrayList<String>();

    public enum SdcardSelector
    {
        BUILT_IN, EXTERNAL
    }

    public void setSdcardItem(SdcardSelector item)
    {
        sdcardItem = item;
    }

    public static String getPhotoPath()
    {
        String absolutePath = null;
        try
        {
            String sdCardDir;
            if (sdcardItem == SdcardSelector.BUILT_IN)
            {
                sdCardDir = SdCardUtils.getFirstExternPath();
            } else
            {
                sdCardDir = SdCardUtils.getSecondExternPath();
                if (sdCardDir == null)
                    return null;
            }
            String photoPath = sdCardDir + "/" + PARENTFOLDER + "/" + PHOTOS + "/";
            File folder = new File(photoPath);
            if (!folder.exists())
            {
                folder.mkdirs();
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            // SimpleDateFormat format = new
            // SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());
            long time = System.currentTimeMillis();
            Date curDate = new Date(time);
            String timeString = format.format(curDate);

            File savePhoto = new File(photoPath + timeString + ".jpg");

            absolutePath = savePhoto.getAbsolutePath();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return absolutePath;
    }

    /**
     * return video path, if the video is not exist, then create it
     *
     * @param parentFolder
     *            like:DCIM/VIDEO
     * @param videoName
     *            like:VIDEO1.AVI
     * @return
     */
    public String getVideoPath(String parentFolder, String videoName)
    {
        String absolutePath = null;
        try
        {
            String sdCardDir;
            if (sdcardItem == SdcardSelector.BUILT_IN)
            {
                sdCardDir = SdCardUtils.getFirstExternPath();
            } else
            {
                sdCardDir = SdCardUtils.getSecondExternPath();
                if (sdCardDir == null)
                    return null;
            }
            String videoPath = sdCardDir + "/" + parentFolder + "/";
            File folder = new File(videoPath);
            if (!folder.exists())
            {
                folder.mkdirs();
            }
            File saveVideo = new File(videoPath + videoName);
            if (!saveVideo.exists())
            {
                saveVideo.createNewFile();
            }
            absolutePath = saveVideo.getAbsolutePath();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return absolutePath;
    }

    /**
     * get video name without any params
     *
     * @return video path
     */
    public static String getVideoPath()
    {
        String absolutePath = null;
        try
        {
            String sdCardDir;
            if (sdcardItem == SdcardSelector.BUILT_IN)
            {
                sdCardDir = SdCardUtils.getFirstExternPath();
            } else
            {
                sdCardDir = SdCardUtils.getSecondExternPath();
                if (sdCardDir == null)
                    return null;
            }
            String videoPath = sdCardDir + "/" + PARENTFOLDER + "/" + VIDEOS + "/";
            File folder = new File(videoPath);
            if (!folder.exists())
            {
                folder.mkdirs();
            }

            // SimpleDateFormat format = new
            // SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            long time = System.currentTimeMillis();
            Date curDate = new Date(time);
            String timeString = format.format(curDate);

            File saveVideo = new File(videoPath + timeString + ".mp4");
            // if (!saveVideo.exists())
            // {
            // saveVideo.createNewFile();
            // }
            absolutePath = saveVideo.getAbsolutePath();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return absolutePath;
    }

    /**
     * get video name without any params
     *
     * @return avi video path
     */
    public static String getAVIPath()
    {
        String absolutePath = null;
        try
        {
            String sdCardDir;
            if (sdcardItem == SdcardSelector.BUILT_IN)
            {
                sdCardDir = SdCardUtils.getFirstExternPath();
            } else
            {
                sdCardDir = SdCardUtils.getSecondExternPath();
                if (sdCardDir == null)
                    return null;
            }
            String videoPath = sdCardDir + "/" + PARENTFOLDER + "/" + VIDEOS + "/";
            File folder = new File(videoPath);
            if (!folder.exists())
            {
                folder.mkdirs();
            }

            // SimpleDateFormat format = new
            // SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            long time = System.currentTimeMillis();
            Date curDate = new Date(time);
            String timeString = format.format(curDate);

            File saveVideo = new File(videoPath + timeString + ".avi");
            absolutePath = saveVideo.getAbsolutePath();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return absolutePath;
    }
    /**
     * return the sdcard path
     *
     * @return
     */
    public static String getRootPath()
    {
        String sdCardDir;
        if (sdcardItem == SdcardSelector.BUILT_IN)
        {
            sdCardDir = SdCardUtils.getFirstExternPath();
        } else
        {
            sdCardDir = SdCardUtils.getSecondExternPath();
            if (sdCardDir == null)
                return null;
        }

        return sdCardDir;
    }

    /**
     * save photos use bytes stream
     *
     * @param parentFolder
     *            like:Photo
     * @param photoName
     *            like:IMAGE1.JPG
     * @param imagedata
     *            image bytes stream data
     */

    public static void savePhoto(Context context, String parentFolder, String photoName, byte[] imagedata)
    {
        String sdCardDir = getRootPath();
        if (sdCardDir != null)
        {
            try
            {
                String photoPath = sdCardDir + "/" + parentFolder + "/";
                File folder = new File(photoPath);
                if (!folder.exists())
                {
                    folder.mkdirs();
                }
                File savePhoto = new File(photoPath, photoName);
                if (!savePhoto.exists())
                {
                    savePhoto.createNewFile();
                }
                String absolutePath = savePhoto.getAbsolutePath();
                Log.e("path", absolutePath);

                FileOutputStream fout;

                fout = new FileOutputStream(absolutePath);

                fout.write(imagedata, 0, imagedata.length);
                fout.close();


            } catch (FileNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e)
            {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    public static void savePhotoBmp(Context context, String parentFolder, String photoName, Bitmap bmp)
    {
        String sdCardDir = getRootPath();
        if (sdCardDir != null)
        {
            try
            {
                File folder = new File(parentFolder);
                if (!folder.exists())
                {
                    folder.mkdirs();
                }
                File savePhoto = new File(parentFolder, photoName);
                if (!savePhoto.exists())
                {
                    savePhoto.createNewFile();
                }
                String absolutePath = savePhoto.getAbsolutePath();
                Log.e("path", absolutePath);

                FileOutputStream fout;

                fout = new FileOutputStream(absolutePath);

                bmp.compress(CompressFormat.JPEG, 90, fout);
                fout.close();

            } catch (FileNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e)
            {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    /**
     * s
     * @param context
     * @param bmp
     * @return
     */
    public static String savePhoto(Context context, Bitmap bmp)
    {
        String sdCardDir = getRootPath();
        if (sdCardDir != null)
        {
            try
            {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                long time = System.currentTimeMillis();
                Date curDate = new Date(time);
                String timeString = format.format(curDate);

                String photoPath = sdCardDir + "/" + PARENTFOLDER + "/" + PHOTOS;

                Log.d("PathConfig", "save file:" + photoPath);

                File folder = new File(photoPath);
                if (!folder.exists())
                {
                    folder.mkdirs();
                    Log.d("PathConfig:", "create folder " + photoPath);
                }
                String photoName = timeString + ".jpg";
                File savePhoto = new File(photoPath, photoName);
                if (!savePhoto.exists())
                {
                    savePhoto.createNewFile();
                }
                String absolutePath = savePhoto.getAbsolutePath();
                Log.e("path", absolutePath);

                FileOutputStream fout;

                fout = new FileOutputStream(absolutePath);

                bmp.compress(CompressFormat.JPEG, 80, fout);

                fout.close();

                return absolutePath;
            } catch (FileNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e)
            {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    public static List<String> getImagesList(final File photoPath)
    {
        List<String> photoList = new ArrayList<String>();

        FileFilter filter = new FileFilter()
        {
            @Override
            public boolean accept(File file)
            {
                if (file.isFile()
                        && (file.getAbsolutePath().toLowerCase().endsWith(".bmp")
                        || file.getAbsolutePath().toLowerCase().endsWith(".jpg") || file.getAbsolutePath()
                        .toLowerCase().endsWith(".png")))
                {
                    return true;
                } else
                    return false;
            }
        };

        File[] filterFiles = photoPath.listFiles(filter);
        if (null != filterFiles && filterFiles.length > 0)
        {

            for (File file : filterFiles)
            {
                // 这边对文件进行过滤
                if (photoList.indexOf(file.getAbsolutePath()) == -1)
                {
                    // Log.e(Tag, file.getAbsolutePath());
                    photoList.add(file.getAbsolutePath());
                }

            }
        }
        return photoList;
    }

    public static List<String> getVideosList(final File videoPath)
    {
        videoList.clear();
        getVideoList(videoPath);
        //getVideoListNew(videoPath);
        return videoList;
    }

    /**
     * New Method:新方法不需要再录像前先拍照作为缩略图
     *
     * @param videoPath
     */
    @SuppressLint("DefaultLocale")
    private static void getVideoListNew(final File videoPath)
    {
        List<String> temp = new ArrayList<String>();
        File[] files = videoPath.listFiles();
        if (files != null && files.length > 0)
        {
            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isFile())
                {
                    if (files[i].getAbsolutePath().toLowerCase().endsWith(".avi")
                            || files[i].getAbsolutePath().toLowerCase().endsWith(".3gp")
                            || files[i].getAbsolutePath().toLowerCase().endsWith(".mp4")
                            || files[i].getAbsolutePath().toLowerCase().endsWith(".mkv"))
                    {
                        String absPath = files[i].getAbsolutePath();

                        File videoFile = new File(absPath);
                        if (videoFile.exists())
                        {
                            if (temp.indexOf(videoFile.getAbsolutePath()) == -1)
                            {
                                temp.add(videoFile.getAbsolutePath());
                                videoList.add(videoFile.toString());
                            }
                        }
                    }

                } else
                {
                    if (files[i].isDirectory() && files[i].getPath().indexOf("/.") == -1)
                    {
                        getVideoListNew(files[i]);
                    }
                }
            }
        }
    }

    /**
     * Old Method:老方法是在录像前拍照作为视频缩略图，这样的话要将视频地址替换为其照片地址来显示
     *
     * @param videoPath
     */
    @SuppressLint("DefaultLocale")
    private static void getVideoList(final File videoPath)
    {
        List<String> temp = new ArrayList<String>();
        File[] files = videoPath.listFiles();
        if (files != null && files.length > 0)
        {
            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isFile())
                {
                    if (files[i].getAbsolutePath().toLowerCase().endsWith(".avi")
                            || files[i].getAbsolutePath().toLowerCase().endsWith(".3gp")
                            || files[i].getAbsolutePath().toLowerCase().endsWith(".mp4"))
                    {
                        String lcPath = files[i].getAbsolutePath().toLowerCase();
                        String absPath = files[i].getAbsolutePath();
                        String photopath = null;
                        if (lcPath.contains(".avi"))
                        {
                            photopath = absPath.replace(".avi", ".jpg");
                        } else if (lcPath.contains(".mp4"))
                        {
                            photopath = absPath.replace(".mp4", ".jpg");
                        } else if (lcPath.contains(".3gp"))
                        {
                            photopath = absPath.replace(".3gp", ".jpg");
                        }
                        File photofile = new File(photopath);
                        if (photofile.exists())
                        {
                            if (temp.indexOf(photofile.getAbsolutePath()) == -1)
                            {
                                temp.add(photofile.getAbsolutePath());
                                videoList.add(photofile.toString());
                            }
                        }
                    }

                } else
                {
                    if (files[i].isDirectory() && files[i].getPath().indexOf("/.") == -1)
                    {
                        getVideoList(files[i]);
                    }
                }
            }
        }
    }


    public static int getSdcardAvilibleSize()
    {
        String sdCardDir = getRootPath();
        StatFs stat = new StatFs(new File(sdCardDir).getPath());
		/* 获取block的SIZE */
        long blockSize = stat.getBlockSize();
		/* 空闲的Block的数量 */
        long availableBlocks = stat.getAvailableBlocks();
		/* 返回bit大小值 */
        return (int) (availableBlocks * blockSize / 1024 / 1024);
    }

    public int getSdcardTotalSize()
    {
        String sdCardDir = getRootPath();
        StatFs stat = new StatFs(new File(sdCardDir).getPath());
		/* 获取block的SIZE */
        long blockSize = stat.getBlockSize();
		/* 空闲的Block的数量 */
        long blockCount = stat.getBlockCount();
		/* 返回bit大小值 */
        return (int) (blockCount * blockSize / 1024 / 1024);
    }

    /** 把录像按修改时间的先后排序 */
    public List<File> sortVideoList(List<File> photoList)
    {
        Collections.sort(photoList, new Comparator<File>()
        {

            @Override
            public int compare(File curFile, File nextFile)
            {
                // TODO Auto-generated method stub
                long firstDate = curFile.lastModified();
                long nextDate = nextFile.lastModified();
                return (firstDate > nextDate) ? 1 : -1; // 若大于，即后修改，返回1，即按修改时间顺序
            }
        });
        return photoList;
    }

    /** delete all the files in the folder and it's sub folders */
    public static void deleteFiles(File file)
    {
        if (file.exists())
        {
            if (file.isFile())
            {
                file.delete();
            } else if (file.isDirectory())
            {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++)
                {
                    deleteFiles(files[i]);
                }
            }
            file.delete();
        }
    }

}
