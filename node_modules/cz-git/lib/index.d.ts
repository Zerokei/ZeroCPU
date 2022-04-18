/**
 * @description: customizable and git support commitizen adapter
 * @author: @Zhengqbbb (zhengqbbb@gmail.com)
 * @license: MIT
 * @copyright: Copyright (c) 2022-present Qiubin Zheng
 * TODO: add more test to protect code
 */
import type { CommitizenType } from "./share";
export * from "./share";
export declare const prompter: (cz: CommitizenType, commit: (message: string) => void) => void;
