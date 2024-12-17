<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>

        <input type="text" id="input1"/>
        <input type="text" id="input2"/>
        <button onclick="sendRequest();">Click me</button>
    </body>
    <script>
        function sendRequest() {
            let input1 = document.getElementById("input1").value;
            let input2 = document.getElementById("input2").value;

            const formData = new FormData();
            formData.append("input1", input1);
            formData.append("input2", input2);

            const req = new XMLHttpRequest();

            req.onreadystatechange = function () {
                if (req.readyState == 200) {
                    const response = req.responseText;
                    console.log(response);
                }
            }

            req.open("POST", "ServerSide", true);
            req.send(formData);
        }
    </script>
</html>
