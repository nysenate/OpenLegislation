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
        tomcatWeb: '/usr/share/tomcat/webapps/legislation',  // <-- CHANGE THIS AS NEEDED
        docsSourceRoot: '../../../docs',
        docsDestRoot: 'static/docs',

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
                         '<%= bowerRoot %>/fullcalendar/fullcalendar.css',
                         '<%= bowerRoot %>/angular-material/angular-material.min.css']
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
            options: {
                mangle: false,
                preserveComments: 'some', // Preserve licensing comments
                banner: '/*! <%= pkg.name %> - v<%= pkg.version %> - ' + '<%= grunt.template.today("yyyy-mm-dd") %> */',
                beautify: false
            },
            vendor: {
                files: {
                    '<%= jsDest %>/vendor.min.js': [
                        // Much dependencies
                        '<%= bowerRoot %>/modernizr/modernizr.js',
                        '<%= bowerRoot %>/jquery/dist/jquery.min.js',
                        '<%= bowerRoot %>/angular/angular.js',
                        '<%= bowerRoot %>/angular-route/angular-route.min.js',
                        '<%= bowerRoot %>/angular-resource/angular-resource.min.js',
                        '<%= bowerRoot %>/angular-animate/angular-animate.min.js',
                        '<%= bowerRoot %>/angular-aria/angular-aria.min.js',
                        '<%= bowerRoot %>/angular-material/angular-material.min.js',
                        '<%= bowerRoot %>/angular-smart-table/dist/smart-table.min.js',
                        '<%= bowerRoot %>/ngInfiniteScroll/build/ng-infinite-scroll.js',
                        '<%= bowerRoot %>/moment/min/moment.min.js',
                        '<%= bowerRoot %>/angular-ui-calendar/src/calendar.js',
                        '<%= bowerRoot %>/fullcalendar/fullcalendar.js',
                        //'<%= bowerRoot %>/amcharts/dist/amcharts/amcharts.js',
                        //'<%= bowerRoot %>/amcharts/dist/amcharts/serial.js',
                        '<%= bowerRoot %>/sockjs/sockjs.min.js',
                        '<%= bowerRoot %>/stomp-websocket/lib/stomp.min.js',
                        '<%= bowerRoot %>/angular-utils-pagination/dirPagination.js',
                        '<%= bowerRoot %>/google-diff-match-patch/diff_match_patch.js',
                        '<%= bowerRoot %>/angular-diff-match-patch/angular-diff-match-patch.js'
                    ]
                }
            },
            app: {
                files: {
                    '<%= jsDest %>/app.min.js':
                        [
                        // Core
                        '<%= jsSource %>/app.js',
                        '<%= jsSource %>/core.js',
                        '<%= jsSource %>/routes.js',
                        '<%= jsSource %>/api.js',

                        // Dashboard
                        '<%= jsSource %>/component/content/dashboard.js',

                        // Bills
                        '<%= jsSource %>/component/content/bill/bill.js',
                        '<%= jsSource %>/component/content/bill/bill-utils.js',
                        '<%= jsSource %>/component/content/bill/bill-filters.js',
                        '<%= jsSource %>/component/content/bill/bill-directives.js',
                        '<%= jsSource %>/component/content/bill/bill-search-ctrl.js',
                        '<%= jsSource %>/component/content/bill/bill-updates-ctrl.js',
                        '<%= jsSource %>/component/content/bill/bill-view-ctrl.js',

                        // Calendars
                        '<%= jsSource %>/component/content/calendar/calendar.js',
                        '<%= jsSource %>/component/content/calendar/calendar-filters.js',
                        '<%= jsSource %>/component/content/calendar/calendar-directives.js',
                        '<%= jsSource %>/component/content/calendar/calendar-view-ctrl.js',
                        '<%= jsSource %>/component/content/calendar/calendar-browse-ctrl.js',
                        '<%= jsSource %>/component/content/calendar/calendar-search-ctrl.js',
                        '<%= jsSource %>/component/content/calendar/calendar-updates-ctrl.js',

                        // Agendas
                        '<%= jsSource %>/component/content/agenda/agenda.js',
                        '<%= jsSource %>/component/content/agenda/agenda-browse-ctrl.js',
                        '<%= jsSource %>/component/content/agenda/agenda-search-ctrl.js',
                        '<%= jsSource %>/component/content/agenda/agenda-updates-ctrl.js',
                        '<%= jsSource %>/component/content/agenda/agenda-view-ctrl.js',
                        '<%= jsSource %>/component/content/agenda/agenda-filters.js',
                        '<%= jsSource %>/component/content/agenda/agenda-directives.js',

                        // Laws
                        '<%= jsSource %>/component/content/law/law.js',
                        '<%= jsSource %>/component/content/law/law-search-ctrl.js',
                        '<%= jsSource %>/component/content/law/law-updates-ctrl.js',
                        '<%= jsSource %>/component/content/law/law-view-ctrl.js',

                        // Transcripts
                        '<%= jsSource %>/component/content/transcript/transcript.js',
                        '<%= jsSource %>/component/content/transcript/transcript-search-ctrl.js',
                        '<%= jsSource %>/component/content/transcript/transcript-hearing-view-ctrl.js',
                        '<%= jsSource %>/component/content/transcript/transcript-session-view-ctrl.js',

                        // SpotChecks
                        '<%= jsSource %>/component/report/spotcheck-base.js',
                        '<%= jsSource %>/component/report/spotcheck-detail.js',
                        '<%= jsSource %>/component/report/spotcheck-summary.js',
                        '<%= jsSource %>/component/report/spotcheck-report.js',
                        '<%= jsSource %>/component/report/spotcheck-mismatch.js',
                        '<%= jsSource %>/component/report/spotcheck-mismatch-view.js',
                        '<%= jsSource %>/component/report/spotcheck-open-summary.js',

                        // Admin
                        '<%= jsSource %>/component/admin/admin.js',
                        '<%= jsSource %>/component/admin/dashboard.js',
                        '<%= jsSource %>/component/admin/logs.js',
                        '<%= jsSource %>/component/admin/cache.js',
                        '<%= jsSource %>/component/admin/account.js',
                        '<%= jsSource %>/component/admin/notification_sub.js',
                        '<%= jsSource %>/component/admin/environment.js']
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
            },
            docs : {
                files: [{
                    expand:true, cwd: '<%= docsDestRoot %>/', src: ['**'], filter: 'isFile',
                    dest: '<%= tomcatWeb %>/static/docs/'
                }]
            }
        },

        shell: {
            docs:  {
                command: 'make html',
                options: {
                    stderr: false,
                    execOptions: {
                        cwd: '../../../docs'
                    }
                }
            }
        },

        /** Automatically run certain tasks based on file changes */
        watch: {
            css: {
                files: ['<%= scssRoot %>/*.scss'],
                tasks: ['compass', 'concat', 'cssmin', 'copy:css', 'beep']
            },
            jsp: {
                files: ['<%= jspSource %>/**/*.jsp', '<%= tagSource %>/**/*.tag'],
                tasks: ['copy:jsp', 'beep']
            },
            js: {
                files: ['<%= jsSource %>/**/*.js'],
                tasks: ['uglify:app', 'copy:js', 'beep']
            },
            docs: {
                files: ['<%= docsSourceRoot %>/*.rst', '<%= docsSourceRoot %>/conf.py'],
                tasks: ['shell:docs', 'copy:docs']
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
    grunt.loadNpmTasks('grunt-shell');
    grunt.loadNpmTasks('grunt-beep');

    grunt.registerTask('default', ['compass', 'concat', 'cssmin', 'uglify', 'copy']);
};