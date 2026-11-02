import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import path from 'path'

export default defineConfig(({ mode }) => {
  // 沿用 Vue2 工程的 VUE_APP_ 前缀，业务代码里的 process.env.VUE_APP_xxx 不用改
  const env = loadEnv(mode, process.cwd(), 'VUE_APP_')
  const baseApi = env.VUE_APP_BASE_API || '/dev-api'
  const serverUrl = env.VUE_APP_SERVER_URL || 'http://localhost:8080/'
  const publicPath = env.VUE_APP_PUBLIC_PATH || '/'
  const port = Number(process.env.port || process.env.npm_config_port || 81)
  // 页面标题：按 mode 读取 .env.development / .env.production / .env.staging 中的 VUE_APP_TITLE
  const htmlTitle = env.VUE_APP_TITLE || 'fuint汽车会员系统'

  return {
    base: publicPath,
    // svg 图标由 src/assets/icons/index.js 用 import.meta.glob 自行注入，无需插件
    // 本行业版保留了低代码表单构建器（views/tool/build），其中部分组件用 JSX 写 render
    plugins: [
      vue(),
      vueJsx(),
      {
        name: 'html-inject-title',
        transformIndexHtml: (html) => html.replace(/<%= VUE_APP_TITLE %>/g, htmlTitle),
      },
    ],
    resolve: {
      // Vite 默认 extensions 不含 .vue，而本工程大量使用 import App from './App' 这类省略写法
      extensions: ['.mjs', '.js', '.mts', '.ts', '.jsx', '.tsx', '.json', '.vue'],
      alias: {
        '@': path.resolve(process.cwd(), 'src'),
      },
    },
    define: {
      'process.env.VUE_APP_BASE_API': JSON.stringify(baseApi),
      'process.env.VUE_APP_SERVER_URL': JSON.stringify(serverUrl),
      'process.env.VUE_APP_TITLE': JSON.stringify(htmlTitle),
      'process.env.VUE_APP_PUBLIC_PATH': JSON.stringify(publicPath),
    },
    server: {
      host: '0.0.0.0',
      port: port,
      open: true,
      allowedHosts: ['0.0.0.0', 'localhost'],
      proxy: {
        [baseApi]: {
          target: serverUrl,
          changeOrigin: true,
          rewrite: (p) => p.replace(new RegExp('^' + baseApi), ''),
        },
      },
    },
    css: {
      preprocessorOptions: {
        scss: {
          // element-plus 与业务样式都用 dart-sass
          api: 'modern-compiler',
        },
      },
    },
    build: {
      outDir: 'dist',
      assetsDir: 'static',
      chunkSizeWarningLimit: 2000,
      rollupOptions: {
        output: {
          chunkFileNames: 'static/js/[name]-[hash].js',
          entryFileNames: 'static/js/[name]-[hash].js',
          assetFileNames: 'static/[ext]/[name]-[hash].[ext]',
        },
      },
    },
  }
})
