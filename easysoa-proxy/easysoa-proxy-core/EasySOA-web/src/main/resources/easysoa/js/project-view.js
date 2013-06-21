var _speed = 300;

function toggle_rep(){
    var $this = $(this);
    if($this.hasClass("rep_open")){
        $this
        .removeClass("rep_open")
        .unbind("click")
        .next()
        .slideUp(_speed,function(){
            $this.bind("click",toggle_rep);
        })
        .find(".rep_open")
        .removeClass("rep_open")
        .next()
        .slideUp(_speed);
    }else{
        $this
        .addClass("rep_open")
        .next()
        .css("border-left","1px dotted #999")
        .slideDown(_speed);
    }
    return false;
}

$(function(){
    $(".rep")
    .bind("click",toggle_rep)
    .next()
    .hide()
    .end()
})

$(function(){
    $(".rep_open")
    .next()
    .show()
    .end()
})





