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
        tomcatWeb: '/usr/share/tomcat7/webapps/legislation',  // <-- CHANGE THIS AS NEEDED
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
            all: {
                options: {
                    mangle: false,
                    preserveComments: 'some', // Preserve licensing comments
                    banner: '/*! <%= pkg.name %> - v<%= pkg.version %> - ' + '<%= grunt.template.today("yyyy-mm-dd") %> */',
                    beautify: false
                },
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
                        //'<%= bowerRoot %>/hammerjs/hammer.min.js',
                        '<%= bowerRoot %>/angular-material/angular-material.min.js',
                        '<%= bowerRoot %>/angular-smart-table/dist/smart-table.min.js',
                        '<%= bowerRoot %>/ngInfiniteScroll/build/ng-infinite-scroll.js',
                        '<%= bowerRoot %>/moment/min/moment.min.js',
                        '<%= bowerRoot %>/angular-ui-calendar/src/calendar.js',
                        '<%= bowerRoot %>/fullcalendar/fullcalendar.js',
                        //'<%= bowerRoot %>/amcharts/dist/amcharts/amcharts.js',
                        //'<%= bowerRoot %>/amcharts/dist/amcharts/serial.js',
                        '<%= bowerRoot %>/angular-utils-pagination/dirPagination.js',
                        '<%= bowerRoot %>/google-diff-match-patch/diff_match_patch.js',
                        '<%= bowerRoot %>/angular-diff-match-patch/angular-diff-match-patch.js'
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
                tasks: ['compass', 'concat', 'cssmin', 'copy:css']
            },
            jsp: {
                files: ['<%= jspSource %>/**/*.jsp', '<%= tagSource %>/**/*.tag'],
                tasks: ['copy:jsp']
            },
            js: {
                files: ['<%= jsSource %>/**/*.js'],
                tasks: ['copy:js']
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

    grunt.registerTask('default', ['compass', 'concat', 'cssmin', 'uglify', 'copy']);
};