function TradeApi () {
    this.call = function(uri, callback, resultProcFunc, params) {
        $.ajax({
            url: uri
            , type: "POST"
            , dataType: 'json'
            , contentType:"application/json; charset=UTF-8"
            , data: JSON.stringify(params)
            , success: function (result) {
                resultProcFunc(result.data);
                return callback(result);
            }
            , error:function(e){
                var data = new Object();
                data.code = 1000;
                data.msg = JSON.parse(e.responseText).message;
                return callback(data);
            }
        });
        return true;
    };

    this.buy = function(callback, params) {
        return this.call("/api/trade/buy", callback, function(data) {
        }, params);
    };

    this.sell = function(callback, params) {
        return this.call("/api/trade/sell", callback, function(data) {
        }, params);
    };

    this.cancel = function(callback, params) {
        return this.call("/api/trade/cancel", callback, function(data) {
        }, params);
    };

    this.getHogas = function(callback, params) {
        return this.call("/api/trade/getHogas", callback, function(data) {
        }, params);
    };

    this.getMyOrders = function(callback, params) {
        return this.call("/api/trade/getMyOrders", callback, function(data) {
        }, params);
    };

    this.getMarketHistoryOrders = function(callback, params) {
        return this.call("/api/trade/getMarketHistoryOrders", callback, function(data) {
        }, params);
    };

    this.getMyHistoryOrders = function(callback, params) {
        return this.call("/api/trade/getMyHistoryOrders", callback, function(data) {
        }, params);
    };
}

var tradeApi = new TradeApi();