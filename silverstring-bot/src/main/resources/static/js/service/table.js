var storageFields = {};
var storageAddFields = {};
var storageEditFields = {};
var storageDeleteFields = {};

var rows = {};
var pageNo = 0;
var pageSize = 10;

function attachDataInTable(title, modifyTitle, getApi, addApi, editApi, delApi, fields, addFields, editFields, deleteFields, pageNo, pageSize, showAction) {
    if (fields == null) {
        fields = storageFields;
        addFields = storageAddFields;
        editFields = storageEditFields;
        deleteFields = storageDeleteFields;
    } else {
        storageFields = fields;
        storageAddFields = addFields;
        storageEditFields = editFields;
        storageDeleteFields = deleteFields;
    }

    pageNo = this.pageNo;
    pageSize = this.pageSize;

    attachAddBtn(title, addApi);
    attachHeaders(fields, showAction);

    var params = new Object();
    params.pageNo = pageNo;
    params.pageSize = pageSize;

    $.ajax({
        url: getApi
        , type: "POST"
        , dataType: 'json'
        , contentType:"application/json; charset=UTF-8"
        , data: JSON.stringify(params)
        , success: function (result) {
            var table = $('#tableBodyId');

            var div = "";
            rows = result.data.contents;
            for (var i in result.data.contents) {
                var actions = "";
                if (showAction == true) {
                    actions = '<td><button type="button" class="btn btn-sm btn-info" onclick="onEditClick' + '(' + "'" + title + "'" + ',' + "'" + editApi + "'" + ',' + 'rows[' + i + ']' + ');">' + modifyTitle + '</button> '
                        + '<button type="button" class="btn btn-sm btn-danger" onclick="onDeleteClick' + '(' + "'" + title + "'" + ',' + "'" + delApi + "'" + ',' + 'rows[' + i + ']' + ');">삭제</button></td>';
                }
                div += "<tr>" + actions;
                div += drawRowTable(fields, result.data.contents[i]);
                div += "</tr>";
            }

            table.html(div);

            attachPage(title, modifyTitle, getApi, addApi, editApi, delApi, fields, addFields, editFields, deleteFields, pageNo, pageSize, result.data, showAction);
        }
    });
}

function call(type, api) {
    var isExit = false;
    var params = formToObj(document.getElementById(type + 'FormId'));
    Object.keys(params).forEach(function(key) {
        if (params[key] == "") {
            if (isExit == false) {
                alert(key + " 필드를 정확히 입력해주세요");
                isExit = true;
            }
        }
    });

    if (isExit == true) {
        $('#' + type + 'DataModal').modal('hide');
        return;
    }

    $.ajax({
        url: api
        , type: "POST"
        , dataType: 'json'
        , contentType:"application/json; charset=UTF-8"
        , data: JSON.stringify(params)
        , success: function (result) {
            if (result.code == "0000") {
                alert("성공하였습니다.");
                $('#' + type + 'DataModal').modal('hide');
                utils.pageReload();
            } else {
                alert("실패하였습니다. [" + result.msg + "]");
                $('#' + type + 'DataModal').modal('hide');
            }
        }
    });
}

function onEditClick(title, api, content) {
    var cell = "<form id='editFormId'>";
    $.each( storageEditFields, function( key, value ) {

        cell += '<div class="row">';
        cell += '<div class="col-md-12">';
        cell += '<label for="field-3" class="control-label">' + value + '</label>';

        var _content = convertKey(key, content);

        if (key == 'service') {
            cell += '<select name="' + key + '" class="form-control">';
            cell += '<option value="BITHUMB" ' + checkSelectedComboBox(content[key], 'BITHUMB') + '>' + 'BITHUMB' + '</option>';
            cell += '</select>';
        } else if (key == 'status') {
            cell += '<select name="' + key + '" class="form-control">';
            cell += '<option value="INACTIVE" ' + checkSelectedComboBox(content[key], 'INACTIVE') + '>' + 'INACTIVE' + '</option>';
            cell += '<option value="ACTIVE" ' + checkSelectedComboBox(content[key], 'ACTIVE') + ' >' + 'ACTIVE' + '</option>';
            cell += '</select>';
        } else if (key == 'content') {
            cell += '<textarea name="' + key + '" class="form-control autogrow" id="field-7" style="overflow: hidden; word-wrap: break-word; resize: horizontal; height: 104px;">' + _content + '</textarea>';
        } else {
            var isEnable = "";
            if (key == "id" || key == "userId") {
                isEnable = 'disabled';
            }
            if (key == "user.level") {
                key = "level";
            }

            cell += '<input name="' + key + '" type="text" class="form-control" id="field-3" value="' + _content + '" ' + isEnable + '>';
        }
        cell += '</div>';
        cell += '</div>';
        cell += '<br/>';

    });
    cell += "</form>";
    $('#editSubmitBtn').attr("onclick", "call('edit','" + api + "');");
    showPopup("edit", cell, title, api);
}

function checkSelectedComboBox(value, condition) {
    if (value == condition) {
        return 'selected';
    }
    return '';
}

function onDeleteClick(title, api, content) {

    var cell = "<form id='delFormId'>";
    $.each( storageDeleteFields, function( key, value ) {

        cell += '<div class="row">';
        cell += '<div class="col-md-12">';
        cell += '<label for="field-3" class="control-label">' + value + '</label>';
        if (key == 'content') {
            cell += '<textarea name="' + key + '" class="form-control autogrow" id="field-7" style="overflow: hidden; word-wrap: break-word; resize: horizontal; height: 104px;">' + content[key] + '</textarea>';
        } else {
            var isEnable = "";
            if (key == "id" || key == "userId") {
                isEnable = 'disabled';
            }
            cell += '<input name="' + key + '" type="text" class="form-control" id="field-3" value="' + content[key] + '" ' + isEnable + '>';
        }
        cell += '</div>';
        cell += '</div>';
        cell += '<br/>';

    });
    cell += "</form>";
    $('#delSubmitBtn').attr("onclick", "call('del','" + api + "');");
    showPopup("del", cell, title, api);
}

function onAddClick(title, api, content) {

    var cell = "<form id='addFormId'>";
    $.each( storageAddFields, function( key, value ) {
        cell += '<div class="row">';
        cell += '<div class="col-md-12">';
        cell += '<label for="field-3" class="control-label">' + value + '</label>';

        if (key == 'service') {
            cell += '<select name="' + key + '" class="form-control">';
            cell += '<option value="BITHUMB" ' + checkSelectedComboBox(content[key], 'BITHUMB') + '>' + 'BITHUMB' + '</option>';
            cell += '</select>';
        } else if (key == 'status') {
            cell += '<select name="' + key + '" class="form-control">';
            cell += '<option value="INACTIVE" ' + checkSelectedComboBox(content[key], 'INACTIVE') + '>' + 'INACTIVE' + '</option>';
            cell += '<option value="ACTIVE" ' + checkSelectedComboBox(content[key], 'ACTIVE') + ' >' + 'ACTIVE' + '</option>';
            cell += '</select>';
        } else if (key == 'content') {
            cell += '<textarea name="' + key + '" class="form-control autogrow" id="field-7" style="overflow: hidden; word-wrap: break-word; resize: horizontal; height: 104px;"></textarea>';
        } else {
            cell += '<input name="' + key + '" type="text" class="form-control" id="field-3">';
        }
        cell += '</div>';
        cell += '</div>';
        cell += '<br/>';

    });
    cell += "</form>";
    $('#addSubmitBtn').attr("onclick", "call('add','" + api + "');");
    showPopup("add", cell, title, api);
}

function attachHeaders(fields, showAction) {
    var css = "";
    if (showAction == false) {
        css = "style=\"display:none;\"";
    }
    var headersContent = '<th class="sorting" ' + css + ' tabindex="0" aria-controls="datatable-keytable" rowspan="1" colspan="1" aria-label="Office: activate to sort column ascending" style="color:gray;width: 140px;">' + "액션" + '</th>';;
    $.each( fields, function( key, value ) {
        headersContent += '<th class="sorting" tabindex="0" aria-controls="datatable-keytable" rowspan="1" colspan="1" aria-label="Office: activate to sort column ascending" style="color:gray;">' + value + '</th>';
    });

    $("#tableHeaderId").html(headersContent);
}

function attachAddBtn(title, addApi) {
    $("#attachAddBtn").attr('onclick', "onAddClick('" + title + "','" + addApi + "','Y'" + ");");
    if (addApi == null) {
        $("#attachAddBtn").css("display", "none");
    }
}

function attachPage(title, modifyTitle, getApi, addApi, editApi, delApi, fields, addFields, editFields, deleteFields, pageNo, pageSize, data, showAction) {
    //page
    var pageObj = $("#datatable_paginate");
    var rows = '<li class="paginate_button previous disabled" aria-controls="datatable-keytable" tabindex="0" id="datatable-keytable_previous">' +
        '<a href="#">이전</a>' +
        '</li>';

    for (var page = 1;page <= data.pageTotalCnt; page++) {
        var className = "paginate_button";
        if (page -1 == data.pageNo) {
            className += " active";
        }
        rows += '<li class="' + className + '" aria-controls="datatable-keytable" tabindex="0">' +
            '<a href="#" onclick="attachDataInTable('
            + "'" + title + "',"
            + "'" + modifyTitle + "',"
            + "'" + getApi + "',"
            + "'" + addApi + "',"
            + "'" + editApi + "',"
            + "'" + delApi + "',"
            + fields + ','
            + addFields + ','
            + editFields + ','
            + deleteFields + ','
            + (page-1) +','
            + pageSize + ','
            + showAction
            + '); return false">' + page + '</a>' +
            '</li>';
    }

    rows += '<li class="paginate_button" aria-controls="datatable-keytable" tabindex="0">' +
        '<a href="#">다음</a>' +
        '</li>';
    pageObj.html(rows);
}

function convertKey(key, row) {
    if (key == "coin") {
        return row["coin"]["name"];
    }


    var _row = "";
    if (key.indexOf(".") > 0) {
        var keys =  key.split( '.' );
        if (row[keys[0]] == null) {
            _row = "";
        } else {
            _row = row[keys[0]][keys[1]];
        }
    } else {
        if (row[key] == null) {
            _row = "";

        } else {
            _row = row[key];
        }
    }
    return _row;
}
function drawRowTable(fields, row) {
    var cell = "";
    var _row = "";
    $.each( fields, function( key, value ) {
        if (key.indexOf(".") > 0) {
            var keys =  key.split( '.' );
            if (row[keys[0]] == null) {
                _row = "";
            } else {
                _row = row[keys[0]][keys[1]];
            }
        } else {
            if (row[key] == null) {
                _row = "";
            } else {
                _row = row[key];
            }
        }
        if (key == "levelIdcardUrlHash") {
            cell += "<td><img src='/api/doc/idcard/" + _row + "'></td>";
        }
        else if (key == "levelDocUrlHash") {
            cell += "<td><img src='/api/doc/doc/" + _row + "'></td>";
        }
        else {
            cell += "<td>" + _row + "</td>";
        }

    });
    return cell;
}

function showPopup(prefix, bodyhtml, title, api) {

    if (prefix == "add") {
        title = title + " 추가";
    }  else if(prefix == "del") {
        title = title + " 삭제";
    }  else if(prefix == "edit") {
        title = title + " 수정";
    }

    $('#' + prefix + 'DataModalLabel').text(title);

    var html = '<div class="form-group">';
    html += bodyhtml;
    html += "<div>";

    $('#' + prefix + 'DataModalBody').html(html);
    $('#' + prefix + 'SubmitBtn').attr("onclick", "call('" + prefix + "','" + api + "');");
    $('#' + prefix + 'DataModal').modal();
}