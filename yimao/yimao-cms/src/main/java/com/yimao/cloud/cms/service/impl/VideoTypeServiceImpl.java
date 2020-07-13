package com.yimao.cloud.cms.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.cms.mapper.VideoMapper;
import com.yimao.cloud.cms.mapper.VideoTypeMapper;
import com.yimao.cloud.cms.po.Video;
import com.yimao.cloud.cms.po.VideoType;
import com.yimao.cloud.cms.service.VideoTypeService;
import com.yimao.cloud.pojo.dto.cms.VideoTypeDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/21
 */
@Service
public class VideoTypeServiceImpl implements VideoTypeService {

    @Resource
    private VideoTypeMapper videoTypeMapper;
    @Resource
    private VideoMapper videoMapper;

    @Override
    public PageVO<VideoTypeDTO> videoTypeList(Integer typeId, Integer platform, Integer level, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<VideoTypeDTO> liveTypePage = videoTypeMapper.videoTypeList(typeId, platform, level);
        if (liveTypePage != null && CollectionUtil.isNotEmpty(liveTypePage.getResult())) {
            List<VideoTypeDTO> typeDTOList;
            for (VideoTypeDTO dto : liveTypePage.getResult()) {
                typeDTOList = videoTypeMapper.videoTypeList(dto.getId(), platform, level);
                dto.setVideoTypeList(typeDTOList);
            }
        }
        return new PageVO<>(pageNum, liveTypePage);
    }

    @Override
    public void addLiveType(VideoTypeDTO videoTypeDTO) {
        if (videoTypeDTO.getParentId() == null) {
            videoTypeDTO.setParentId(0);   //没有上级，就是一级分类
            videoTypeDTO.setLevel(1);
        } else {
            videoTypeDTO.setLevel(2);
        }
        if (videoTypeDTO.getSorts() == null) {
            videoTypeDTO.setSorts(0);
        }
        videoTypeDTO.setDeleteFlag(false);
        videoTypeDTO.setCreateTime(new Date());
        videoTypeDTO.setUpdateTime(new Date());
        VideoType videoType = new VideoType(videoTypeDTO);
        videoTypeMapper.insertSelective(videoType);
    }

    @Override
    public void updateLiveType(VideoTypeDTO videoTypeDTO) {
        if (videoTypeDTO.getParentId() == null) {
            videoTypeDTO.setParentId(0);   //没有上级，就是一级分类
            videoTypeDTO.setLevel(1);
        }
        videoTypeDTO.setUpdateTime(new Date());
        VideoType videoType = new VideoType(videoTypeDTO);
        videoTypeMapper.updateByPrimaryKeySelective(videoType);
    }

    @Override
    public void deleteLiveType(Integer id) {
        VideoType videoType = videoTypeMapper.selectByPrimaryKey(id);
        if (videoType != null) {
            //查询是否存在子栏目
            Example example = new Example(VideoType.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("parentId", id);
            List<VideoType> liveTypes = videoTypeMapper.selectByExample(example);
            if (CollectionUtil.isNotEmpty(liveTypes)) {
                throw new BadRequestException("此栏目存在子栏目不能直接删除！");
            }
            //查询是否存在视频
            Example examLive = new Example(Video.class);
            Example.Criteria criteriaLive = examLive.createCriteria();
            Example.Criteria criteriaLive1 = examLive.createCriteria();
            criteriaLive.andEqualTo("liveTypeId", id);
            criteriaLive1.andEqualTo("liveTypeSubId", id);
            examLive.or(criteriaLive1);
            List<Video> ltList = videoMapper.selectByExample(examLive);
            if (CollectionUtil.isNotEmpty(ltList)) {
                throw new BadRequestException("此栏目下存在视频不能直接删除！");
            }
            videoTypeMapper.deleteByPrimaryKey(id);
            return;
        }
        throw new BadRequestException("删除失败，对象不存在！");
    }

    @Override
    public VideoTypeDTO getLiveType(Integer id) {
        VideoType videoType = videoTypeMapper.selectByPrimaryKey(id);
        if (videoType == null) {
            throw new NotFoundException("未找到该分类");
        }
        VideoTypeDTO videoTypeDTO = new VideoTypeDTO();
        videoType.convert(videoTypeDTO);
        return videoTypeDTO;
    }

    @Override
    public List<VideoTypeDTO> getAllVideoType() {
        List<VideoTypeDTO> list = videoTypeMapper.getAllVideoType();
        return list;
    }
}
