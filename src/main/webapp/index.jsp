<%@ page language="java"  contentType="text/html; charset=UTF-8" %>

<html>
    <body>
        springmvc上传文件
        <form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
                <input type="file" name="upload_file" />
                <input type="file" name="upload_file" />
                <input type="submit" value="springmvc上传文件" />
        </form>
    </body>
</html>
