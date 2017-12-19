function Utils () {
    this.numberWithCommas = function(x) {
        return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    };

    this.isEmpty = function (val) {
        var dressedVal = val.replace(/\s/g, "");
        if (dressedVal == null || dressedVal.length <= 0) {
            return true;
        }
        return false;
    };

    this.showInfoAlert = function (content) {
        $.Notification.notify("info",'top center', "INFORMATION", content);
    };
    this.showErrorAlert = function (content) {
        $.Notification.notify("error",'top center', "(>_<) ERROR", content);
    };

    this.validateEmail = function (email) {
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
    };

    this.successAlert = function (title, text, doneFunction) {
        swal({
            title: title,
            text: text,
            type: "success",
            showCancelButton: false,
            confirmButtonClass: 'btn-success waves-effect waves-light',
            confirmButtonText: '닫기',
        }, doneFunction);
    }

    this.errorAlert = function (title, text, doneFunction) {
        swal({
            title: title,
            text: text,
            type: "error",
            showCancelButton: false,
            confirmButtonClass: 'btn-danger waves-effect waves-light',
            confirmButtonText: '닫기'
        }, doneFunction);
    }

    this.infoAlert = function (title, text, doneFunction) {
        swal({
            title: title,
            text: text,
            type: "info",
            showCancelButton: false,
            confirmButtonClass: 'btn-success waves-effect waves-light',
            confirmButtonText: '닫기'
        }, doneFunction);
    }

    this.pageReload = function () {
        window.location.reload();
    }

    this.convertStatusToMessage = function (status) {
        if (status == "PENDING") {
            return "대기중";
        } else if (status == "APPROVAL") {
            return "승인";
        } else if (status == "COMPLETED") {
            return "완료";
        } else if (status == "FAILED") {
            return "실패";
        } else if (status == "CANCEL") {
            return "취소";
        }
    }
}

var utils = new Utils();