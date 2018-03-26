/* Show nice arrows in accordions */
$('.accordion button').click(function () {
    if($(this).hasClass("collapsed")) {
        $(this).find('.arrow').removeClass("fa-angle-right").addClass("fa-angle-down");
    } else {
        $(this).find('.arrow').removeClass("fa-angle-down").addClass("fa-angle-right");
    }
});