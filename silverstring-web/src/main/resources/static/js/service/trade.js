function isInt(el) {
    if (isNaN(parseInt(el)) ||
        (parseInt(el) != parseFloat(el))) {
        return false;
    }
    return true;
}

function loadCoinAvgPrice() {
    var params = new Object();
    params.coin = $('#selectionCoin').val();

    commonApi.getCoinAvgPrice(function(data) {
        $("#coinAvgPriceId").text(utils.numberWithCommas(data.price));
        var content = "";
        if (data.marker == "+") {
            $("#coinAvgMarkAndPercentId").css("color", "indianred");
            content = data.marker + utils.numberWithCommas(data.gapPrice) + " (" + data.marker + data.changePercent + "%)";
        } else if (data.marker == "-") {
            $("#coinAvgMarkAndPercentId").css("color", "#00b19d");
            content = data.marker + utils.numberWithCommas(data.gapPrice) + " (" + data.marker + data.changePercent + "%)";
        } else {
            $("#coinAvgMarkAndPercentId").css("color", "black");
            content = "-- (0%)";
        }
        $("#coinAvgMarkAndPercentId").text(content);

    }, params);
}

function selectHogaOrder(type, price) {
    $("#" + type + "Price").val(price);
    determine(type, 0);
}

function determine(type, percent) {

    //value
    var price = $("#" + type + "Price").val();
    var formData = formToObj(document.getElementById(type + 'FormId'));
    var availableBalance = 0;
    if (type == "buy") {
        availableBalance = formData.krwWalletAvailableBalance;
    } else if (type == "sell") {
        availableBalance = formData.walletAvailableBalance;
    } else {
        return;
    }

    var amount = 0;
    if (percent == null || percent == 0) {
        var amountVal = $("#" + type + "Amount").val();
        if (amountVal == 0 || amountVal == "") {
            amountVal = 0;
        }
        amount = amountVal;
    } else {
        if (type == 'sell') {
            amount = (availableBalance / price * percent).toFixed(8);
        } else {
            amount = (availableBalance / price * percent / 100).toFixed(8);
        }
    }
    $("#" + type + "Amount").val(amount);

    //balance
    var balance = determineBalance(type);
    if (type == "sell") {
        amount= balance;
    }

    //fee
    var fee = (amount * formData.tradingFeePercent / 100).toFixed(8);
    $("#" + type + "Fee").text(fee);

    //total balance
    var totalBalance = (amount - fee).toFixed(8);
    $("#" + type + "TotalBalance").text(totalBalance);

    return balance;
}

function determineBalance(type) {
    var price = $("#" + type + "Price").val();
    if (price != "" && isInt(price) != true) {
        $("#" + type + "Price").val(0);
        return;
    }

    var amount = $("#" + type + "Amount").val();
    var balance = (price * amount).toFixed(8);
    $("#" + type + "Balance").text(balance);
    return balance;
}

function loadMyWalletInfo() {
    walletApi.getMyWalletInfos(function(result) {
        if (result.code == 0000) {
            var content1 = "<tr>";
            var content2 = "<tr>";
            for(var index in result.data.infos) {
                var info = result.data.infos[index];
                var img = "<img style=\"width:18px;height:18px;margin-right: 5px;\" src=\"" + info.coin.logoUrl + "\"/>";
                content1 += "<td>" + img + info.coin.hanName + "(" + info.coin.name + ")" + "</td>";
                content2 += "<td style='color:black;font-weight:bold;'>" + info.wallet.availableBalance + " " + "<span style='color:gray'>" + info.coin.unit + "</span>";
                if (info.wallet.usingBalance > 0) {
                    content2 += " (" + info.wallet.usingBalance + " 사용중) " + "</td>";
                } else {
                    content2 += "</td>"
                }

                var selectedCoin = $('#selectionCoin').val();
                if (info.coin.name == "KRW") {
                    $("#krwWalletAvailableBalanceDisplayFromBuy").text(info.wallet.availableBalance);
                    $("#krwWalletAvailableBalanceFromBuy").val(info.wallet.availableBalance);
                    $("#krwWalletAvailableBalanceFromSell").val(info.wallet.availableBalance);
                } else if (info.coin.name == selectedCoin) {
                    $("#walletAvailableBalanceDisplayFromSell").text(info.wallet.availableBalance);
                    $("#walletAvailableBalanceFromBuy").val(info.wallet.availableBalance);
                    $("#walletAvailableBalanceFromSell").val(info.wallet.availableBalance);
                }
            }
            content1 += "</tr>";
            content2 += "</tr>";
            $("#myWalletInfoBodyId").html(content1 + content2);
        } else {
            utils.errorAlert("지갑정보 가져오기에 실패하였습니다.", result.msg, null);
        }
    }, null);
}

function loadHogaOrders(pageSize) {
    var params = new Object();
    params.fromCoin = $('#selectionCoin').val();
    params.toCoin = "KRW";
    params.sellPageNo = 0;
    params.sellPageSize = pageSize;
    params.buyPageNo = 0;
    params.buyPageSize = pageSize;

    tradeApi.getHogas(function(result) {
        if (result.code == 0000) {
            if (result.data.buy.length > 0) {
                //buy
                var content1 = "";
                for (var index in result.data.buy) {
                    var row = result.data.buy[index];
                    var num = index;
                    var color = "indianred";
                    var id = "buyRow"+index;
                    content1 += "<tr id='" + id + "' style=\"cursor:pointer;font-weight:bold\" onclick=\"javascript:selectHogaOrder('buy', " + row.price + ")\">";
                    content1 += "<td style='color:" + color + "'>" + num + "</td>";
                    content1 += "<td style='color:" + color + "'>" + row.price + "</td>";
                    content1 += "<td style='color:" + color + "'>" + row.amount + "</td>";
                    content1 += "</tr>";
                }

                $("#buyHogaBodyId").html(content1);

                //$('tr:eq(1), tr:eq(2), tr:eq(3)', '.myTable').find('td:first').css('background', 'red')
                //background-color:#e4b9c0;
               /*$("#buyRow0").animate( {backgroundColor:'#aa0000'}, 100).fadeOut(200,function() {
                    $("#buyRow0").fadeIn(200);
                    //$("#buyRow0").animate( {backgroundColor:'#000000'}, 10)
                });*/
                //$("#buyRow0").css("background-color", "#ff0000 !important;");
            } else {
                $("#buyHogaBodyId").html("");
            }

            if (result.data.sell.length > 0) {
                //sell
                var content2 = "";
                for (var index in result.data.sell) {
                    var row = result.data.sell[index];
                    var num = index;
                    var color = "#00b19d";
                    content2 += "<tr style=\"cursor:pointer;font-weight:bold\" onclick=\"javascript:selectHogaOrder('sell', " + row.price + ")\">";
                    content2 += "<td style='color:" + color + "'>" + num + "</td>";
                    content2 += "<td style='color:" + color + "'>" + row.price + "</td>";
                    content2 += "<td style='color:" + color + "'>" + row.amount + "</td>";
                    content2 += "</tr>";
                }

                $("#sellHogaBodyId").html(content2);
            } else {
                $("#sellHogaBodyId").html("");
            }
        } else {
            utils.errorAlert("가져오기에 실패하였습니다.", result.msg, null);
        }
    }, params);
}

function loadMarketHistoryOrders(pageSize) {
    var params = new Object();
    params.fromCoin = $('#selectionCoin').val();
    params.pageNo = 0;
    params.pageSize = pageSize;

    tradeApi.getMarketHistoryOrders(function (result) {
        if (result.code == 0000) {
            if (result.data.historyOrders.length == 0) {return;}

            var content = "";
            for (var index in result.data.historyOrders) {
                var row = result.data.historyOrders[index];
                var num = index;
                var orderType = "";
                var color = 'black';
                if (row.orderType == "BUY") {
                    color = 'indianred';
                    orderType = "구매";
                } else if (row.orderType == "SELL") {
                    color = '#00b19d';
                    orderType = "판매";
                }

                content += "<tr>";
                content += "<td style='color:" + color + "'>" + row.completedDtm + "</td>";
                content += "<td style='color:" + color + "'>" + orderType + "</td>";
                content += "<td style='color:" + color + "'>" + row.price + "</td>";
                content += "<td style='color:" + color + "'>" + row.amount + "</td>";
                content += "</tr>";
            }

            $("#marketHistoryBodyId").html(content);
        } else {
            utils.errorAlert("가져오기에 실패하였습니다.", result.msg, null);
        }
    }, params);
}



function loadMyHistoryOrders(pageNo, pageSize) {
    var params = new Object();
    params.fromCoin = $('#selectionCoin').val();
    params.pageNo = pageNo;
    params.pageSize = pageSize;

    tradeApi.getMyHistoryOrders(function (result) {
        if (result.code == 0000) {

            var content = "<tr><td>아직 거래내역이 없습니다.<td></tr>";
            if (result.data.historyOrders.length > 0) {
                content = "";
                for (var index in result.data.historyOrders) {
                    var row = result.data.historyOrders[index];
                    var num = index;
                    var orderType = "";
                    var coin = "";
                    var color = 'black';
                    if (row.orderType == "BUY") {
                        color = 'indianred';
                        orderType = "구매";
                        coin = row.toCoin.name;
                    } else if (row.orderType == "SELL") {
                        color = '#00b19d';
                        orderType = "판매";
                        coin = row.fromCoin.name;
                    }

                    content += "<tr>";
                    content += "<td style='color:" + color + "'>" + row.completedDtm + "</td>";
                    content += "<td style='color:" + color + "'>" + coin + "</td>";
                    content += "<td style='color:" + color + "'>" + orderType + "</td>";
                    content += "<td style='color:" + color + "'>" + row.price + "</td>";
                    content += "<td style='color:" + color + "'>" + row.amount + "</td>";
                    content += "</tr>";
                }
            }

            $("#myHistoryBodyId").html(content);


            //page
            var pageObj = $("#myHistoryOrders-datatable-keytable_paginate");
            rows = '<li class="paginate_button previous disabled" aria-controls="datatable-keytable" tabindex="0" id="datatable-keytable_previous">' +
                '<a href="#">이전</a>' +
                '</li>';

            for (var page = 1;page <= result.data.pageTotalCnt; page++) {
                var className = "paginate_button";
                if (page -1 == result.data.pageNo) {
                    className += " active";
                }
                rows += '<li class="' + className + '" aria-controls="datatable-keytable" tabindex="0">' +
                    '<a href="#" onclick="loadMyHistoryOrders(' + (page-1) + ',' + pageSize + '); return false">' + page + '</a>' +
                    '</li>';
            }

            rows += '<li class="paginate_button" aria-controls="datatable-keytable" tabindex="0">' +
                '<a href="#">다음</a>' +
                '</li>';
            pageObj.html(rows);


        } else {
            utils.errorAlert("가져오기에 실패하였습니다.", result.msg, null);
        }
    }, params);
}

function loadMyOrders(pageNo) {
    var params = new Object();
    params.fromCoin = $('#selectionCoin').val();
    params.pageNo = pageNo;
    params.pageSize = 10;
    tradeApi.getMyOrders(function (result) {
        if (result.code == 0000) {
            //buy
            var content = "<tr><td>아직 주문이 없습니다.<td></tr>";
            if (result.data.orders.length > 0) {
                content = "";
                for (var index in result.data.orders) {

                    var row = result.data.orders[index];
                    var num = index;
                    var orderType = "";
                    var coin = "";
                    var color = 'black';
                    if (row.orderType == "BUY") {
                        color = 'indianred';
                        orderType = "구매";
                        coin = row.toCoin.name;
                    } else if (row.orderType == "SELL") {
                        color = '#00b19d';
                        orderType = "판매";
                        coin = row.fromCoin.name;
                    }

                    content += "<tr>";
                    content += "<td style='color:" + color + "'>" + row.regDtm + "</td>";
                    content += "<td style='color:" + color + "'>" + coin + "</td>";
                    content += "<td style='color:" + color + "'>" + orderType + "</td>";
                    content += "<td style='color:" + color + "'>" + row.price + "</td>";
                    content += "<td style='color:" + color + "'>" + row.amountRemaining + " (" + row.amount + ")" + "</td>";
                    content += "<td>" + '<button type="button" class="btn btn-danger waves-effect waves-light w-md btn-xs m-b-1" onclick="javascript:cancelOrder(' + row.id + ')">주문취소</button>' + "</td>";
                    content += "</tr>";
                }
            }

            $("#myOrderBodyId").html(content);

            //page
            var pageObj = $("#myOrders-datatable-keytable_paginate");
            rows = '<li class="paginate_button previous disabled" aria-controls="datatable-keytable" tabindex="0" id="datatable-keytable_previous">' +
                '<a href="#">이전</a>' +
                '</li>';

            for (var page = 1;page <= result.data.pageTotalCnt; page++) {
                var className = "paginate_button";
                if (page -1 == result.data.pageNo) {
                    className += " active";
                }
                rows += '<li class="' + className + '" aria-controls="datatable-keytable" tabindex="0">' +
                    '<a href="#" onclick="loadMyOrders(' + (page-1) + '); return false">' + page + '</a>' +
                    '</li>';
            }

            rows += '<li class="paginate_button" aria-controls="datatable-keytable" tabindex="0">' +
                '<a href="#">다음</a>' +
                '</li>';
            pageObj.html(rows);

        } else {
            utils.errorAlert("가져오기에 실패하였습니다.", result.msg, null);
        }
    }, params);
}