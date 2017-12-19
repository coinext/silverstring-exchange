var sock = new SockJS(BASE_URL + '/websock');
var client = Stomp.over(sock);
client.connect({}, function(frame) {
    console.log('connected stomp over sockjs');
    client.subscribe('/topic/exchange', function(messagePacket) {
        var payload = JSON.parse(messagePacket.body);
        //console.log("================== " + payload);
        var cmd = payload.cmd;
        var scope = payload.scope;
        var coin = payload.coin;
        var userId = payload.userId;
        var data = payload.data;
        var selectedCoin = $('#selectionCoin').val();
        var selectedUserId = $('#userId').val();

        console.log("cmd ================== " + cmd);
        if (cmd == "CHART") {
            if (selectedCoin == coin) {
                loadCoinAvgPrice();
                console.log("!!!!=====",data[0] + "," +data[1]+ "," +data[2]+ "," +data[3]+ "," +data[4]);
                series.addPoint([data[0], data[1], data[2], data[3], data[4]], true, true);
                //series.addPoint([1512272980000 + i,375,375,372.2,372.52], true, true);
            }
        }

        if (cmd == "TRADE") {
            loadCoinAvgPrice();
            loadHogaOrders(10);
            loadMarketHistoryOrders(7);

            if (selectedUserId == userId) {
                loadMyWalletInfo();
                loadMyHistoryOrders(0, 10);
                loadMyOrders(0);
            }
        }
    });
});