package cn.dingyuegroup.gray.server.web;

import cn.dingyuegroup.gray.server.manager.RbacManager;
import cn.dingyuegroup.gray.server.model.vo.GrayRbacUserVO;
import cn.dingyuegroup.gray.server.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by 170147 on 2019/1/22.
 */
@RestController
@RequestMapping("/gray/manager/rbac")
public class GrayRbacController extends BaseController {
    @Autowired
    private RbacManager rbacManager;

    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    public ResponseEntity<List<GrayRbacUserVO>> listUser() {
        List<GrayRbacUserVO> list = rbacManager.list(null);
        return ResponseEntity.ok(list);
    }
}
