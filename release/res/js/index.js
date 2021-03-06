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
            var uptime = data.Uptime;
            var cpu = data.CPU;
            var mem = data.Mem;
            var swap = data.Swap;
            var net = data.Net;
            var disk = data.Disk;

            $("#cpuUseage").html((100 - (cpu.idle * 100)).toFixed(2) + "%");
            $("#loadAvg").html(uptime.loadAvg);
            $("#upTime").html(uptime.upTime);
            $("#memUsed .value").html((mem.used / 1024 / 1024).toFixed(2) + "MB");
            $("#memFree .value").html((mem.free / 1024 / 1024).toFixed(2) + "MB");
            $("#memUsage .value").html(mem.usage.toFixed(3) + "%");
            $("#swapUsed .value").html((swap.used / 1024 / 1024).toFixed(2) + "MB");
            $("#swapFree .value").html((swap.free / 1024 / 1024).toFixed(2) + "MB");
            var rx = net.rx;
            var tx = net.tx;
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
            for (var i = 0; i < disk.length; i++) {
                var diskInfo = disk[i];
                $("#diskList .info").append("<div>" + diskInfo.name + " 读取:" + parseFloat(diskInfo.read).toFixed(2) + "MB/s 写入:" + parseFloat(diskInfo.write).toFixed(2) + "MB/s 磁盘使用:" + diskInfo.usage + "</div>");
            }
            setTimeout(dynStart, 1000);
        },
        error: function (xhr, textStatus) {
            $("#btn_Start").attr("disabled", false);
        }
    });
}

function infoStart() {
    $.get("api?passwd=" + passwd + "&command=info", function (data) {
        $("#cpuModel").html(data.cpuModel);
    });
}