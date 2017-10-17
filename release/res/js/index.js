var passwd = "";

function start() {
    if (passwd == "") {
        passwd = $("#passwd").val();
    }
    infoStart();
    dynStart();
    $("#btn_Start").attr("disabled", true);
}

function dynStart() {
    $.ajax({
        url: 'api',
        type: 'GET', //GET
        async: true,    //或false,是否异步
        data: {
            passwd: passwd,
            command: "dyn"
        },
        timeout: 5000,    //超时时间
        dataType: 'json',
        success: function (data, textStatus, jqXHR) {
            // $.get("api?passwd=" + passwd + "&command=dyn", function (data) {
//                console.log(data);
            $("#cpuUseage").html((100 - (data.cpuIdle * 100)).toFixed(2) + "%");
            $("#loadAvg").html(data.loadAvg);
            $("#upTime").html(data.upTime);
            $("#memUsed .value").html((data.usedMem / 1024 / 1024).toFixed(2) + "MB");
            $("#memFree .value").html((data.freeMem / 1024 / 1024).toFixed(2) + "MB");
            var rx = data.rx;
            var tx = data.tx;
            var rxNetStatus = "KB/s";
            var txNetStatus = "KB/s";
            if (rx > 1024) {
                rx = rx / 1024;
                rxNetStatus = "MB/s";
            }
            if (tx > 1024) {
                tx = tx / 1024;
                txNetStatus = "MB/s";
            }
            $("#rx .value").html(rx.toFixed(3) + rxNetStatus);
            $("#tx .value").html(tx.toFixed(3) + txNetStatus);

            $("#diskList .info").html("");
            for (var i = 0; i < data.diskList.length; i++) {
                var diskInfo = data.diskList[i];
                $("#diskList .info").append("<div>" + diskInfo.name + " 读取:" + parseFloat(diskInfo.read).toFixed(2) + "MB/s 写入:" + parseFloat(diskInfo.write).toFixed(2) + "MB/s 磁盘使用:" + diskInfo.usage + "</div>");
            }
            setTimeout(dynStart, 1000);
            // });
        },
        error: function (xhr, textStatus) {
            $("#btn_Start").attr("disabled", false);
        }
    });
}

function infoStart() {
    $.get("api?passwd=" + passwd + "&command=info", function (data) {
        // console.log(data);
        $("#cpuModel").html(data.cpuModel);
    });
}