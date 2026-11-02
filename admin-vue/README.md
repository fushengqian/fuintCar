## 开发

### 环境要求
- Node.js >= 20.19.0（Vite 5 自身兼容 18.x / 20+，但项目依赖的 sass 1.x 要求 Node >= 20.19.0，故 16.x / 18.x 不可用）
- npm >= 10.0.0（Node 20+ 自带；如需升级：`npm i -g npm@latest`）
- 最低版本：Node.js 20.19.0
- 推荐版本：Node.js 22.x 或 24.x（LTS 版本）
- 已验证环境：Node.js 24.21.0 + npm 11.19.0（四个版本构建均通过）
- npm 镜像（可选，解决下载慢）：`npm config set registry https://registry.npmmirror.com`

### 快速开始

```bash

# 进入项目目录
# 推荐使用 nvm 管理 Node.js 版本
# 例如：nvm install 22 && nvm use 22（本项目未内置 .nvmrc，需自行指定版本）

cd fuintAdmin

# 安装依赖
npm install

# 建议不要直接使用 cnpm 安装依赖，会有各种诡异的 bug。可以通过如下操作解决 npm 下载速度慢的问题
npm install --registry=https://registry.npmmirror.com

# 启动服务
npm run dev
```

浏览器访问 http://localhost:8081

## 发布

```bash
# 构建测试环境
npm run build:stage

# 构建生产环境
npm run build:prod

# 清除缓存
npm cache clean --force
```