function CommonApi () {
    this.call = function(uri, callback, resultProcFunc, params) {
        $.ajax({
            url: uri
            , type: "POST"
            , dataType: 'json'
            , contentType:"application/json; charset=UTF-8"
            , data: JSON.stringify(params)
            , success: function (result) {
                resultProcFunc(result.data);
                return callback(result.data);
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

    this.ticker = function(callback, params) {
        return this.call("/api/common/ticker", callback, function(data) {
        }, params);
    }

    this.getTopN1Notice = function(callback, params) {
        return this.call("/api/common/getTopN1Notice", callback, function(data) {
        }, params);
    }

    this.getCoinAvgPrice = function(callback, params) {
        return this.call("/api/common/getCoinAvgPrice", callback, function(data) {
        }, params);
    }

    this.getAllCoinAvgPrices = function(callback, params) {
        return this.call("/api/common/getAllCoinAvgPrices", callback, function(data) {
        }, params);
    }

    this.getAllIcoRecommend = function(callback, params) {
        return this.call("/api/common/getAllIcoRecommend", callback, function(data) {
        }, params);
    }

    this.get24hGraphData = function(callback, params) {
        return this.call("/api/common/get24hGraphData", callback, function(data) {
        }, params);
    }
}

var commonApi = new CommonApi();