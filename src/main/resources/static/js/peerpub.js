/* Show nice arrows in accordions */
$('.card .collapse').on('hide.bs.collapse', function () {
    $(this).siblings('.card-header').find('.arrow').removeClass("fa-angle-down").addClass("fa-angle-right");
});
$('.card .collapse').on('show.bs.collapse', function () {
    $(this).siblings('.card-header').find('.arrow').removeClass("fa-angle-right").addClass("fa-angle-down");
});

/* Enable tooltips globally */
$(function () {
  $('[data-toggle="tooltip"]').tooltip()
})