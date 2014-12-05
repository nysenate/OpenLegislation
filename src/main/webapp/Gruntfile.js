module.exports = function(grunt) {
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        /** Path locations to be used as templates */
        cssRoot: 'static/css',
        cssSource: '<%= cssRoot %>/src',
        cssDest: '<%= cssRoot %>/dest',
        bowerRoot: 'static/bower_components',
        scssRoot: 'WEB-INF/scss',
        jsRoot: 'static/js',
        jsSource: '<%= jsRoot %>/src',
        jsDest: '<%= jsRoot %>/dest',
        jspSource: 'WEB-INF/view',
        tagSource: 'WEB-INF/tags',
        tomcatWeb: '/usr/share/tomcat7/webapps/legislation',


        /** Compile SCSS files into css and place them into the css source directory */
        compass: {
            dev: {
                /** Configured in config.rb */
            }
        },

        /** Combine all the required css assets into one file. */
        concat: {
            css: {
                files: {
                    '<%= cssDest %>/main.css':
                        ['<%= cssSource %>/*.css',
                         '<%= bowerRoot %>/pace/themes/blue/pace-theme-minimal.css',
                         '<%= bowerRoot %>/ng-table/ng-table.min.css']
                }
            }
        },

        /** Minify the main combined css. */
        cssmin: {
            css: {
                files: {
                    '<%= cssDest %>/main.min.css': ['<%= cssDest %>/main.css']
                }
            }
        },

        /** Compress all js into dev and prod files */
        uglify: {
            all: {
                options: {
                    mangle: false,
                    preserveComments: 'some', // Preserve licensing comments
                    banner: '/*! <%= pkg.name %> - v<%= pkg.version %> - ' + '<%= grunt.template.today("yyyy-mm-dd") %> */',
                    beautify: false
                },
                files: {
                    '<%= jsDest %>/vendor.min.js': [
                        '<%= bowerRoot %>/jquery/dist/jquery.min.js',
                        '<%= bowerRoot %>/foundation/js/foundation.min.js',
                        '<%= bowerRoot %>/angular/angular.min.js',
                        '<%= bowerRoot %>/angular-route/angular-route.min.js',
                        '<%= bowerRoot %>/angular-resource/angular-resource.min.js',
                        '<%= bowerRoot %>/angular-foundation/mm-foundation-tpls.min.js',
                        '<%= bowerRoot %>/ng-table/ng-table.js',
                        '<%= bowerRoot %>/moment/min/moment.min.js',
                        '<%= bowerRoot %>/pace/pace.min.js',
                        '<%= bowerRoot %>/highcharts/highcharts.js',
                        '<%= bowerRoot %>/angular-ui-calendar/src/calendar.js',
                        '<%= bowerRoot %>/fullcalendar/fullcalendar.min.js'
                    ]//'<%= jsDest %>/main.min.js': ['<%= jsSource %>/**/*.js']
                }
            }
        },

        copy: {
            css: {
                files: [{
                    expand:true, cwd: '<%= cssDest %>/', src: ['**'], filter: 'isFile',
                    dest: '<%= tomcatWeb %>/static/css/dest/'
                }]
            },
            js: {
                files: [{
                    expand:true, src: ['<%= jsSource %>/**', '<%= jsDest %>/**'], filter: 'isFile',
                    dest: '<%= tomcatWeb %>'}]
            },
            jsp : {
                files: [{
                    expand:true, src: ['<%= jspSource %>/**', '<%= tagSource %>/**'], filter: 'isFile',
                    dest: '<%= tomcatWeb %>'
                }]
            }
        },

        /** Automatically run certain tasks based on file changes */
        watch: {
            css: {
                files: ['<%= scssRoot %>/*.scss'],
                tasks: ['compass', 'concat', 'cssmin', 'copy:css']
            },
            jsp: {
                files: ['<%= jspSource %>/**/*.jsp', '<%= tagSource %>/**/*.tag'],
                tasks: ['copy:jsp']
            },
            js: {
                files: ['<%= jsSource %>/**/*.js'],
                tasks: ['copy:js']
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-compass');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-contrib-watch');

    grunt.registerTask('default', ['compass', 'concat', 'cssmin', 'uglify', 'copy']);
};