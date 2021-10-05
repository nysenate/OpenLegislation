module.exports = {
  purge: [
    './WEB-INF/app/**/*.js',
    './WEB-INF/app/**/*.html',
  ],
  darkMode: false, // or 'media' or 'class'
  theme: {
    extend: {
      colors: {
        blue: {
          100: '#c4e7f8',
          200: '#97cee7',
          300: '#4bafdd',
          400: '#2694C7',
          500: '#008CBA',
          600: '#1F77A1',
          700: '#2B6A90',
          800: '#165B81',
          900: '#0e3a4e',
        }
      },
      borderWidth: {
        '1': '1px'
      },
    },
  },
  variants: {
    extend: {
      borderWidth: ['responsive', 'hover', 'focus'],
      translate: ['responsive', 'hover', 'focus', 'active'],
    },
  },
  plugins: [],
}
