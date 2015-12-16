var calendarModule = angular.module('open.calendar');

calendarModule.filter('sectionDisplayName', function() {
    var sectionNameMap = {
        'ORDER_OF_THE_FIRST_REPORT' : 'First Report',
        'ORDER_OF_THE_SECOND_REPORT' : 'Second Report',
        'ORDER_OF_THE_SPECIAL_REPORT' : 'Special Report',
        'THIRD_READING_FROM_SPECIAL_REPORT' : 'Third Reading from Special Report',
        'THIRD_READING' : 'Third Reading',
        'STARRED_ON_THIRD_READING' : 'Starred on Third Reading'
    };

    return function(input) {
        if (sectionNameMap.hasOwnProperty(input)) {
            return sectionNameMap[input];
        }
        else return '* ' + input;
    };
});

var sectionArray = [
    'ORDER_OF_THE_FIRST_REPORT',
    'ORDER_OF_THE_SECOND_REPORT',
    'ORDER_OF_THE_SPECIAL_REPORT',
    'THIRD_READING_FROM_SPECIAL_REPORT',
    'THIRD_READING',
    'STARRED_ON_THIRD_READING'
];
calendarModule.filter('orderBySection', function() {
    return function(obj) {
        var array = [];
        Object.keys(obj).forEach(function(key) { array.push(obj[key]); });
        array.sort(function(a, b) {
            return sectionArray.indexOf(a.items[0].sectionType) - sectionArray.indexOf(b.items[0].sectionType);
        });
        return array;
    };
});