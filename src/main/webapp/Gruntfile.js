var fs = require("fs");
var libxmljs = require("libxmljs");

var pomPath = __dirname + "/../../../pom.xml";
var pomXml = fs.readFileSync(pomPath, 'utf8');

var pomDoc = libxmljs.parseXml(pomXml);
var artifactId = pomDoc.find("/*/*[name()='artifactId']")[0].text();
var version = pomDoc.find("/*/*[name()='version']")[0].text();

var sass = require('node-sass');

module.exports = function (grunt) {
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        properties: grunt.file.readJSON('grunt.properties.json'),

        /** Path locations to be used as templates */
        cssRoot: 'static/css',
        cssSource: '<%= cssRoot %>/src',
        cssDest: '<%= cssRoot %>/dest',
        bowerRoot: 'static/bower_components',
        scssRoot: 'static/scss',
        jsRoot: 'static/js',
        jsSource: '<%= jsRoot %>/src',
        jsDest: '<%= jsRoot %>/dest',
        jspSource: 'WEB-INF/view',
        tagSource: 'WEB-INF/tags',

        buildName: artifactId + '##' + version,
        tomcatWeb: '<%= properties.deployDirectory %>/<%= buildName %>',
        docsSourceRoot: '../../../docs/api',
        docsDestRoot: 'static/docs',

        // Compile sass into css
        sass: {
            options: {
                implementation: sass,
                sourceMap: true
            },
            openleg: {
                files: {
                    '<%= cssSource %>/openleg.css': '<%= scssRoot %>/app.scss'
                }
            }
        },

        // Run postprocessors on css
        postcss: {
            options: {
                map: true, // inline sourcemaps

                processors: [
                    require('pixrem')(), // add fallbacks for rem units
                    require('autoprefixer')({browsers: 'last 2 versions'}), // add vendor prefixes
                    require('cssnano')() // minify the result
                ]
            },
            openleg: {
                src: '<%= cssSource %>/openleg.css',
                dest: '<%= cssDest %>/openleg.min.css'
            },
            lib: {
                src: '<%= cssSource %>/lib.css',
                dest: '<%= cssDest %>/lib.min.css'
            }
        },

        /** Combine all the required css assets into one file. */
        concat: {
            css_lib: {
                files: {
                    '<%= cssSource %>/lib.css':
                        ['<%= bowerRoot %>/fullcalendar/fullcalendar.css',
                            '<%= bowerRoot %>/angular-material/angular-material.min.css']
                }
            }
        },

        /** Compress all js into dev and prod files */
        uglify: {
            options: {
                mangle: false,
                preserveComments: 'some', // Preserve licensing comments
                banner: '/*! <%= pkg.name %> - v<%= pkg.version %> - ' + '<%= grunt.template.today("yyyy-mm-dd") %> */',
                sourceMap: true
            },
            vendor: {
                files: {
                    '<%= jsDest %>/vendor.min.js': [
                        // Much dependencies
                        '<%= bowerRoot %>/jquery/dist/jquery.min.js',
                        '<%= bowerRoot %>/angular/angular.min.js',
                        '<%= bowerRoot %>/angular-route/angular-route.min.js',
                        '<%= bowerRoot %>/angular-resource/angular-resource.min.js',
                        '<%= bowerRoot %>/angular-animate/angular-animate.min.js',
                        '<%= bowerRoot %>/angular-aria/angular-aria.min.js',
                        '<%= bowerRoot %>/angular-material/angular-material.min.js',
                        '<%= bowerRoot %>/angular-smart-table/dist/smart-table.min.js',
                        '<%= bowerRoot %>/ngInfiniteScroll/build/ng-infinite-scroll.js',
                        '<%= bowerRoot %>/moment/min/moment.min.js',
                        '<%= bowerRoot %>/angular-ui-calendar/src/calendar.js',
                        '<%= bowerRoot %>/fullcalendar/fullcalendar.min.js',
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
                            '<%= jsSource %>/component/report/spotcheck-detail-window.js',
                            '<%= jsSource %>/component/report/spotcheck-report-page.js',
                            '<%= jsSource %>/component/report/spotcheck-report-page-content-tab.js',
                            '<%= jsSource %>/component/report/api/spotcheck-mismatch-api.js',
                            '<%= jsSource %>/component/report/api/spotcheck-mismatch-summary-api.js',

                            // Admin
                            '<%= jsSource %>/component/admin/admin.js',
                            '<%= jsSource %>/component/admin/dashboard.js',
                            '<%= jsSource %>/component/admin/logs.js',
                            '<%= jsSource %>/component/admin/cache.js',
                            '<%= jsSource %>/component/admin/account.js',
                            '<%= jsSource %>/component/admin/notification_sub.js',
                            '<%= jsSource %>/component/admin/email.js',
                            '<%= jsSource %>/component/admin/environment.js',
                            '<%= jsSource %>/component/admin/reports.js',
                            '<%= jsSource %>/component/admin/indices.js']

                }
            }
        },

        copy: {
            css: {
                files: [{
                    expand: true, cwd: '<%= cssDest %>/', src: ['**'], filter: 'isFile',
                    dest: '<%= tomcatWeb %>/<%= cssDest %>/'
                }]
            },
            js: {
                files: [{
                    expand: true, src: ['<%= jsSource %>/**', '<%= jsDest %>/**'], filter: 'isFile',
                    dest: '<%= tomcatWeb %>'
                }]
            },
            jsp: {
                files: [{
                    expand: true, src: ['<%= jspSource %>/**', '<%= tagSource %>/**'], filter: 'isFile',
                    dest: '<%= tomcatWeb %>'
                }]
            },
            docs: {
                files: [{
                    expand: true, cwd: '<%= docsDestRoot %>/', src: ['**'], filter: 'isFile',
                    dest: '<%= tomcatWeb %>/<%= docsDestRoot %>/'
                }]
            }
        },

        shell: {
            docs: {
                command: 'make html',
                options: {
                    stderr: false,
                    execOptions: {
                        cwd: '<%= docsSourceRoot %>'
                    }
                }
            }
        },

        /** Automatically run certain tasks based on file changes */
        watch: {
            scss: {
                files: ['<%= scssRoot %>/*.scss'],
                tasks: ['css:openleg', '<%= properties.scssBeep %>']
            },
            jsp: {
                files: ['<%= jspSource %>/**/*.jsp', '<%= tagSource %>/**/*.tag'],
                tasks: ['jsp', '<%= properties.jspBeep %>']
            },
            js: {
                files: ['<%= jsSource %>/**/*.js'],
                tasks: ['js:app', '<%= properties.jsBeep %>']
            },
            docs: {
                files: ['<%= docsSourceRoot %>/*.rst', '<%= docsSourceRoot %>/conf.py'],
                tasks: ['docs', '<%= properties.docsBeep %>']
            }
        },

        fileExists: {
            buildDir: '<%= tomcatWeb %>'
        }
    });

    grunt.loadNpmTasks('grunt-sass');
    grunt.loadNpmTasks('grunt-postcss');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-shell');
    grunt.loadNpmTasks('grunt-beep');
    grunt.loadNpmTasks('grunt-file-exists');

    // Tasks that process and copy only one type of source file
    grunt.registerTask('css:openleg', ['sass', 'postcss:openleg', 'fileExists', 'copy:css']);
    grunt.registerTask('css:lib', ['concat', 'postcss:lib', 'fileExists', 'copy:css']);
    grunt.registerTask('js:app', ['uglify:app', 'fileExists', 'copy:js']);
    grunt.registerTask('js:vendor', ['uglify:vendor', 'fileExists', 'copy:js']);
    grunt.registerTask('jsp', ['fileExists', 'copy:jsp']);
    grunt.registerTask('docs', ['shell:docs', 'fileExists', 'copy:docs']);

    // Generate necessary css + js files
    grunt.registerTask('process', ['sass', 'concat', 'postcss', 'uglify']);
    // *DEFAULT* Process and copy only css + js
    grunt.registerTask('default', ['process', 'fileExists', 'copy', 'beep']);
    // Process and copy css + js + docs
    grunt.registerTask('build', ['process', 'docs', 'fileExists', 'copy', 'beep'])
};
