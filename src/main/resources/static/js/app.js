/**
* 01.07.2016
* 12:17
*
* @author nshantaev
*/
$(document).on({
    ajaxStart: function() {
        indicator.showPleaseWait();
        $('#errorNotification').hide();
    },
    ajaxStop: function() {
        indicator.hidePleaseWait();
    }
});

var indicator;
indicator = indicator || (function () {
        return {
            showPleaseWait: function() {
                $("#indicatorModal").modal("show");
            },
            hidePleaseWait: function () {
                $("#indicatorModal").modal("hide");
            }
        };
    })();

$(document).on("change", "#pageSizeSelect", function () {
    $("#pageSize").val(this.value);
    $("#page").val(1);
    search();
});

$(document).on("click", ".pageLink", function () {
    $("#pageSize").val($(this).attr("pageSize"));
    $("#page").val($(this).attr("page"));
    search();
});

$(document).keypress(function(event) {
    if(event.which == 13 && ($(event.target).hasClass("form-control") || $(event.target).hasClass("input-filter")) && $(event.target).is("input")) {
        event.preventDefault();
        $("#page").val(1);
        search();
    }
});

$(document).on("change", "#eventSelect", function (event) {
    event.preventDefault();
    $("#page").val(1);
    search();
});

$(document).on("change", "#eventStatusSelect", function (event) {
    event.preventDefault();
    $("#page").val(1);
    search();
});

function showDetails(url) {
    $("#myModal .modal-dialog").html("");
    $("#myModal .modal-dialog").load(url, function () {
        $("#myModal").modal("show");
    });
}

function create(url){
    $("#myModal .modal-dialog").html("");
    $("#myModal .modal-dialog").load(url, function () {
        $("#myModal").modal("show");
    });
}

function search(){
    $.ajax({
        url: $('#searchForm').attr('action'),
        data: $('#searchForm').serialize(),
        type: "POST",
        success: function (returnedData) {
            $('#searchResults').html(returnedData);
        }
    });
}
$(function() {
    $(window).bind("load resize", function() {
        var topOffset = 50;
        var width = (this.window.innerWidth > 0) ? this.window.innerWidth : this.screen.width;
        if (width < 768) {
            $('div.navbar-collapse').addClass('collapse');
            topOffset = 100; // 2-row-menu
        } else {
            $('div.navbar-collapse').removeClass('collapse');
        }

        var height = ((this.window.innerHeight > 0) ? this.window.innerHeight : this.screen.height) - 1;
        height = height - topOffset;
        if (height < 1) height = 1;
        if (height > topOffset) {
            $("#page-wrapper").css("min-height", (height) + "px");
        }
    });
});


function toFix(number, digits){
    if(number>0 && digits>0){
        return number.toFixed(digits).replace(".",",");
    }
    return '';
}

function msToTime(s) {
    var ms = s % 1000;
    s = (s - ms) / 1000;
    var secs = s % 60;
    s = (s - secs) / 60;
    var mins = s % 60;
    var hrs = (s - mins) / 60;
    hrs = (hrs < 10) ? "0" + hrs : hrs;
    mins = (mins < 10) ? "0" + mins : mins;
    return hrs + ':' + mins;
}

function formatDate(d) {
    return (d.getDate().toString().length == 2 ? d.getDate().toString() : "0" + d.getDate().toString()) + "-" + ((d.getMonth() + 1).toString().length == 2 ? (d.getMonth() + 1).toString() : "0" + (d.getMonth() + 1).toString()) + "-" + d.getFullYear().toString() + " " + (d.getHours().toString().length == 2 ? d.getHours().toString() : "0" + d.getHours().toString()) + ":" + ((parseInt(d.getMinutes() / 5) * 5).toString().length == 2 ? (parseInt(d.getMinutes() / 5) * 5).toString() : "0" + (parseInt(d.getMinutes() / 5) * 5).toString()) + ":00";
}

function notifyMessageByModel(notifyModel) {
    if (notifyModel != null) {
        console.log(notifyModel.notifyMessage);
        notifyMessage(notifyModel.notifyMessage, notifyModel.notifyType.$name);
    }
}

function notifyMessage(notifyText, notifyType) {
    if (notifyText) {
        $.notify({
            // message
            message: notifyText
        }, {
            // notifyType
            type: notifyType != null ? notifyType : 'info'
        });
    }
}