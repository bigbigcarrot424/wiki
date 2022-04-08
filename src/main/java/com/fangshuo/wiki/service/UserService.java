package com.fangshuo.wiki.service;

import com.fangshuo.wiki.domain.User;
import com.fangshuo.wiki.domain.UserExample;
import com.fangshuo.wiki.exception.BusinessException;
import com.fangshuo.wiki.exception.BusinessExceptionCode;
import com.fangshuo.wiki.mapper.UserMapper;
import com.fangshuo.wiki.req.UserQueryReq;
import com.fangshuo.wiki.req.UserSaveReq;
import com.fangshuo.wiki.resp.UserQueryResp;
import com.fangshuo.wiki.resp.PageResp;
import com.fangshuo.wiki.util.CopyUtil;
import com.fangshuo.wiki.util.SnowFlake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req){
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        //当name不为空时
        if(!ObjectUtils.isEmpty(req.getLoginName())){
            criteria.andNameLike("%" + req.getLoginName() + "%");
        }

        PageHelper.startPage(req.getPage(), req.getSize());
        //PageHelper会作用到这个查询上
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("总行数：{}",pageInfo.getTotal());
        LOG.info("总页数：{}",pageInfo.getPages());

        List<UserQueryResp> respList = CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    /**
     * 保存
     */
    public void save(UserSaveReq req){
        User user = CopyUtil.copy(req, User.class);
        if (ObjectUtils.isEmpty(req.getId())){
            User userDB = selectByLoginName(req.getLoginName());
            if (ObjectUtils.isEmpty(userDB)){
                //如果是空，那么新增
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            }else {
                //用户名已存在
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        }else{
            //更新
            User userDB = selectByLoginName(req.getLoginName());
            if (ObjectUtils.isEmpty(userDB)){
                userMapper.updateByPrimaryKey(user);
            }else {
                //用户名已存在
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        }
    }

    public void delete(Long id){
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String LoginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(LoginName);
        List<User> userList = userMapper.selectByExample(userExample);
        if (CollectionUtils.isEmpty(userList)){
            return null;
        }else {
            return userList.get(0);
        }
    }
}
