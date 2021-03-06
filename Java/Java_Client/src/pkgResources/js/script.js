// source for DatePicker: http://www.eyecon.ro/datepicker/

function generateCalendar (fromDate, toDate) {
  removeCalendar();
  $('#simple-calendar').DatePicker({
    mode: 'multiple',
    inline: true,
    calendars: 1,
    dateFrom: fromDate,
    dateTo: toDate
  });
}

function removeCalendar() {
	$('#simple-calendar').empty();
}

function getSelectedDates() {
  return $('#simple-calendar').DatePickerGetDate()[0];
}
/*
$(document).ready(function() {
  generateCalendar(new Date(2015,11,24), new Date(2015,11,29));
});*/
