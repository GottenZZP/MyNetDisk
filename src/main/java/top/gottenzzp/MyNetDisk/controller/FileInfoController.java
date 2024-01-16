package top.gottenzzp.MyNetDisk.controller;

import top.gottenzzp.MyNetDisk.annotation.GlobalInterceptor;
import top.gottenzzp.MyNetDisk.entity.enums.FileCategoryEnums;
import top.gottenzzp.MyNetDisk.entity.enums.FileDelFlagEnums;
import top.gottenzzp.MyNetDisk.entity.query.FileInfoQuery;
import top.gottenzzp.MyNetDisk.entity.po.FileInfo;
import top.gottenzzp.MyNetDisk.entity.vo.FileInfoVO;
import top.gottenzzp.MyNetDisk.entity.vo.PaginationResultVO;
import top.gottenzzp.MyNetDisk.entity.vo.ResponseVO;
import top.gottenzzp.MyNetDisk.service.FileInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author GottenZZP
 * 文件信息表 Controller
 */
@RestController("fileInfoController")
@RequestMapping("/file")
public class FileInfoController extends ABaseController{
	@Resource
	private FileInfoService fileInfoService;

	/**
	 * 加载数据列表
	 *
	 * @param session  会话
	 * @param query    查询
	 * @param category 类别
	 * @return {@link ResponseVO}
	 */
	@RequestMapping("/loadDataList")
	@GlobalInterceptor
	public ResponseVO loadDataList(HttpSession session, FileInfoQuery query, String category) {
		// 搜索文件类别
		FileCategoryEnums categoryEnums = FileCategoryEnums.getByCode(category);
		// 若文件类别不为空，则设置查找条件
		if (categoryEnums != null) {
			query.setFileCategory(categoryEnums.getCategory());
		}
		// 获取当前用户的文件信息
		query.setUserId(getUserInfoFromSession(session).getUserId());
		// 设置排序条件
		query.setOrderBy("last_update_time desc");
		// 设置删除状态为使用中
		query.setDelFlag(FileDelFlagEnums.USING.getFlag());
		// 分页查询
		PaginationResultVO<FileInfo> page = fileInfoService.findListByPage(query);
		// 将分页查询的结果转换为前端需要的格式:FileInfoVO
		return getSuccessResponseVO(convert2PaginationVO(page, FileInfoVO.class));
	}
}