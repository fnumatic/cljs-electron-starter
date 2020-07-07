module.exports = {
   purge: {
     enabled: true,
     content: ['./app/*.html',
               './app/**/*.js'],
   },
    variants: {
      // ...
     pointerEvents: ['responsive',  'focus'],
    }
  }