function loadCurrentCoinInfos() {
    commonApi.getAllCoinAvgPrices(function (data) {
        var content = "";
        for (var index in data) {
            var row = data[index];
            var num = index;
            var orderType = "";
            var color = 'white';
            if (row.marker == "+") {
                color = 'indianred';
            } else if (row.marker == "-") {
                color = '#00b19d';
            }

            //coinInfo
            content += "<tr>";
            content += "<td><img style=\"width:24px;height:24px;\" src=\"" + row.coinInfo.logoUrl + "\"/></td>";
            content += "<td style='color:" + color + "'>" + row.coinInfo.hanName + " (" + row.coinInfo.name + ")" + "</td>";
            content += "<td style='color:" + color + "'>" + row.price + " 원" + "</td>";
            content += "<td style='color:" + color + "'>" + row.marker + row.gapPrice + " 원" + " (" + row.marker + row.changePercent + "%)" + "</td>";
            content += "<td style='color:" + color + "'>" + row.totalTradeAmount24h + " " + row.coinInfo.unit + "</td>";
            content += "</tr>";
        }

        $("#currentCoinBodyId").html(content);
    }, null);
}


function loadCurrentDashboardInfo() {
    commonApi.getDashboardInfo(function (data) {
        $("#userTotalCntId").text(utils.numberWithCommas(data.userTotalCnt));
        $("#totoalKrwTotalBalId").text(utils.numberWithCommas(data.totoalKrwTotalBal));
        $("#depositWaitCntId").text(utils.numberWithCommas(data.depositWaitCnt));
        $("#withdrawalWaitCntId").text(utils.numberWithCommas(data.withdrawalWaitCnt));
    }, null);
}

setInterval(function () {
    loadCurrentCoinInfos();
    loadCurrentDashboardInfo();
}, 1000 * 5);