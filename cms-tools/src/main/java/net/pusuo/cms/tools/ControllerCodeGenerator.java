//package net.pusuo.cms.tools;
//
//import com.sun.codemodel.*;
//import com.sun.codemodel.writer.SingleStreamCodeWriter;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.Date;
//
///**
// * Created with IntelliJ IDEA.
// * User: shijinkui
// * Date: 13-4-23
// * Time: 下午9:43
// * To change this template use File | Settings | File Templates.
// */
//public class ControllerCodeGenerator {
//
//    public static void main(String... args) throws JClassAlreadyExistsException, IOException {
//        String base_package = "com.sohu.smc.pic";
//        String _bean = "GroupPicInfo";
//
//
//        JCodeModel codeModel = new JCodeModel();
//        JDefinedClass jc = codeModel._class(base_package + ".service." + _bean + "Service"); //Creates a new class
//
//        //import ...
//        codeModel.ref(base_package + ".core." + _bean);
//
//        //类注释
//        JDocComment jDocComment = jc.javadoc();
//        jDocComment.add("generated " + _bean + " service, the enter of rest");
//        jDocComment.add("\r\n" + new Date() + "");
//
//        JClass parent = codeModel.ref(AbstractService.class);
//        jc._extends(parent);
//
//
//        /* Adding method body */
////        JBlock jBlock = jc.body();
//
//        //声明变量
//        JClass bean = codeModel.ref(_bean);
//        JClass dao = codeModel.ref(_bean + "Dao");
//        jc.field(JMod.PRIVATE | JMod.FINAL, bean, "dao").init(JExpr.ref("DaoFactory").invoke("getBindStatusDao"));
//
//        JMethod create = jc.method(JMod.PUBLIC, Boolean.class, "create");
//        JVar cb = create.param(bean, "obj");
//        JBlock cbody = create.body();
//
//        cbody.decl(dao, "createDao").init(JExpr.ref("dao").invoke("insert").arg(JExpr.ref("obj")));
//        cbody._return(JExpr.ref("service").invoke("create").arg(cb)); //the return statement
//
//
//        JMethod get = jc.method(JMod.PUBLIC, bean, "get");
//        JVar modeParam = get.param(Long.class, "key");
//        get.body()._return(JExpr.ref("service").invoke("get").arg(modeParam)); //the return statement
//
//        JMethod delete = jc.method(JMod.PUBLIC, UpdateResponse.class, "delete");
//        JVar dp = delete.param(Long.class, "key");
//        delete.body()._return(JExpr.ref("service").invoke("delete").arg(dp)); //the return statement
//
//
//        JMethod update = jc.method(JMod.PUBLIC, UpdateResponse.class, "update");
//        JVar up = update.param(Long.class, "key");
//        JVar b = update.param(bean, "obj");
//        update.body()._return(JExpr.ref("service").invoke("update").arg(up).arg(b)); //the return statement
//
//
//        //output
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        codeModel.build(new SingleStreamCodeWriter(out));
//
//        System.out.println(out.toString());
//    }
//}
