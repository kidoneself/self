package com.yimao.cloud.cms.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.VideoOperationEnum;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.cms.mapper.VideoMapper;
import com.yimao.cloud.cms.mapper.VideoTypeMapper;
import com.yimao.cloud.cms.po.Video;
import com.yimao.cloud.cms.po.VideoType;
import com.yimao.cloud.cms.service.VideoService;
import com.yimao.cloud.pojo.dto.cms.VideoDTO;
import com.yimao.cloud.pojo.dto.cms.VideoTypeDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/21
 */
@Service
@Slf4j
public class VideoServiceImpl implements VideoService {

    @Resource
    private VideoMapper videoMapper;
    @Resource
    private VideoTypeMapper videoTypeMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageVO<VideoDTO> listVideo(Integer liveId, Integer videoTypeId, Integer videoTypeSubId, Integer recommend, Integer liveStatus, Integer deleteFlag, Integer platform, Integer status, String name, Integer sort, Date startReleaseTime, Date endReleaseTime, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<VideoDTO> lives = videoMapper.listVideo(liveId, videoTypeId, videoTypeSubId, recommend, liveStatus, deleteFlag, platform, status, name, sort, startReleaseTime, endReleaseTime);
        if (lives != null && CollectionUtil.isNotEmpty(lives.getResult())) {
            List<VideoTypeDTO> videoTypes;
            VideoType videoType;  //一级
            VideoType videoTypeSub; //二级
            VideoTypeDTO videoTypeDTO;
            VideoTypeDTO videoTypeSubDTO;
            for (VideoDTO videoDTO : lives.getResult()) {
                videoTypes = new ArrayList<>();
                if (videoDTO.getLiveTypeId() != null) {
                    videoType = videoTypeMapper.selectByPrimaryKey(videoDTO.getLiveTypeId());
                    if (videoType != null) {
                        videoTypeDTO = new VideoTypeDTO();
                        videoType.convert(videoTypeDTO);
                        if (videoDTO.getLiveTypeSubId() != null) {
                            videoTypeSub = videoTypeMapper.selectByPrimaryKey(videoDTO.getLiveTypeSubId());
                            videoTypeSubDTO = new VideoTypeDTO();
                            if (videoTypeSub != null) {
                                videoTypeSub.convert(videoTypeSubDTO);
                            }
                            videoTypes.add(videoTypeSubDTO);
                            videoTypeDTO.setVideoTypeList(videoTypes);
                        }
                        videoDTO.setVideoType(videoTypeDTO);
                    }
                }
            }
        }
        return new PageVO<>(pageNum, lives);
    }

    @Override
    public Integer saveVideo(Video video, String currentAdmin) {
        if (video.getLiveStatus() != null) {
            video.setLiveStatus(3);  //默认直播中
        }
        if (StringUtil.isNotEmpty(video.getMobileUrl())) {
            video.setMobileUrl(video.getMobileUrl());
        }
        Date now = new Date();
        if (video.getDeleteFlag() != null && !video.getDeleteFlag()) {
            video.setReleaseTime(now);
        }
        video.setCreateTime(now);
        video.setUpdateTime(now);
        return videoMapper.insertSelective(video);
    }

    @Override
    public Integer updateVideo(Video video, String currentAdmin) {
        video.setUpdateTime(new Date());
        if (StringUtil.isNotEmpty(video.getMobileUrl())) {
            video.setMobileUrl(video.getMobileUrl());
        }
        return videoMapper.updateByPrimaryKeySelective(video);
    }

    @Override
    public void deleteVideo(Integer id) {
        videoMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Video onLine(Integer id) {
        Video live = videoMapper.selectByPrimaryKey(id);
        if (live != null) {
            if (live.getDeleteFlag()) {
                live.setDeleteFlag(false);
                live.setReleaseTime(new Date());
            } else {
                live.setDeleteFlag(true);
            }
            live.setUpdateTime(new Date());
            videoMapper.updateByPrimaryKeySelective(live);
            return live;
        }
        return null;
    }

    @Override
    public void updateCount(Integer id, String type, Integer userId) {
        int count = 1;
        if (Objects.equals(VideoOperationEnum.LIKE.value, type) && userId != null) {
            //这版不做实际的点赞/取消 记录，
//            Likes likes = new Likes();
//            likes.setUserId(info.getId());
//            likes.setSubjectId(Long.parseLong(videoId + ""));
//            likes.setType(3);
//            likes.setCreateTime(new Date());
//            likes.setUpdateTime(new Date());
//            likesMapper.insert(likes);
        }
        //累加
        Map<String, Object> map = new HashMap<>(8);
        map.put("videoId", id);
        map.put("type", type);
        map.put("count", count);
        rabbitTemplate.convertAndSend(RabbitConstant.VIDEO_COUNT_ADD, map);
    }

    @Override
    public VideoDTO videoById(Integer id, Integer userId) {
        Video video = videoMapper.selectByPrimaryKey(id);
        VideoDTO videoDTO = new VideoDTO();
        if (video != null) {
            video.convert(videoDTO);
            //是否点赞
            if (videoDTO.getHasLikes() == null || videoDTO.getHasLikes()) {
                videoDTO.setHasLikes(false);
            } else {
                videoDTO.setHasLikes(true);
            }
        }
        return videoDTO;
    }

    @Override
    public List<VideoTypeDTO> videoTypeList() {
        Example example1 = new Example(VideoType.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("level", 1);
        criteria1.andEqualTo("deleteFlag", false);
        example1.orderBy("sorts");
        List<VideoType> firstLevelTypeList = videoTypeMapper.selectByExample(example1);
        List<VideoTypeDTO> fDtoList = new ArrayList<>();
        VideoTypeDTO typeDTO;
        for (VideoType lt : firstLevelTypeList) {
            typeDTO = new VideoTypeDTO();
            lt.convert(typeDTO);
            fDtoList.add(typeDTO);
        }

        Example example2 = new Example(VideoType.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andEqualTo("level", 2);
        criteria2.andEqualTo("deleteFlag", false);
        example2.orderBy("sorts");
        List<VideoType> secondLevelTypeList = videoTypeMapper.selectByExample(example2);

        if (CollectionUtil.isNotEmpty(secondLevelTypeList) && CollectionUtil.isNotEmpty(firstLevelTypeList)) {
            //转化
            List<VideoTypeDTO> sDtoList = new ArrayList<>();
            VideoTypeDTO sTypeDTO;
            for (VideoType lt : secondLevelTypeList) {
                sTypeDTO = new VideoTypeDTO();
                lt.convert(sTypeDTO);
                sDtoList.add(sTypeDTO);
            }
            for (VideoTypeDTO first : fDtoList) {
                for (VideoTypeDTO second : sDtoList) {
                    if (Objects.equals(second.getParentId(), first.getId())) {
                        first.getVideoTypeList().add(second);
                    }
                }
            }
        }
        VideoTypeDTO recommendType = new VideoTypeDTO();
        recommendType.setName("推荐");
        recommendType.setSorts(0);
        recommendType.setLevel(1);
        recommendType.setRemark("推荐分类");
        recommendType.setDeleteFlag(false);
        fDtoList.add(recommendType);
        return fDtoList;
    }


    @Override
    public void updateVideoCount(Map<String, Object> map) {
        Video video = videoMapper.selectByPrimaryKey(Integer.parseInt(map.get("videoId").toString()));
        if (video != null) {
            String type = map.get("type").toString();
            Integer count = Integer.parseInt(map.get("count").toString());
            if (StringUtil.isNotEmpty(type)) {
                log.info("===============类型：" + type + ",视频ID：" + video.getLiveId() + "================");
                if (Objects.equals(VideoOperationEnum.WATCH.value, type)) {
                    video.setWatchTimes(video.getWatchTimes() != null ? video.getWatchTimes() + count : count);
                    video.setRealWatchTimes(video.getRealWatchTimes() != null ? video.getRealWatchTimes() + count : count);
                } else if (Objects.equals(VideoOperationEnum.LIKE.value, type)) {
                    video.setLikeNum(video.getLikeNum() != null ? video.getLikeNum() + count : count);
                } else if (Objects.equals(VideoOperationEnum.SHARE.value, type)) {
                    video.setShareNum(video.getShareNum() != null ? video.getShareNum() + count : count);
                }
                videoMapper.updateByPrimaryKeySelective(video);
            }
        }
    }

    @Override
    public List<VideoDTO> getVideoByRecommend() {

        return videoMapper.getVideoByRecommend();
    }
}
