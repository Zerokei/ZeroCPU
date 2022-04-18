"use strict";
/**
 * @description: customizable and git support commitizen adapter
 * @author: @Zhengqbbb (zhengqbbb@gmail.com)
 * @license: MIT
 * @copyright: Copyright (c) 2022-present Qiubin Zheng
 * TODO: add more test to protect code
 */
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __exportStar = (this && this.__exportStar) || function(m, exports) {
    for (var p in m) if (p !== "default" && !Object.prototype.hasOwnProperty.call(exports, p)) __createBinding(exports, m, p);
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.prompter = void 0;
// @ts-ignore
var inquirer_autocomplete_prompt_1 = __importDefault(require("inquirer-autocomplete-prompt"));
var loader_1 = require("@cz-git/loader");
var loader_2 = require("./loader");
var until_1 = require("./until");
__exportStar(require("./share"), exports);
var prompter = function (cz, commit) {
    (0, loader_1.commitilintConfigLoader)().then(function (clConfig) {
        var options = (0, loader_2.generateOptions)(clConfig);
        var questions = (0, loader_2.generateQuestions)(options, cz);
        cz.registerPrompt("autocomplete", inquirer_autocomplete_prompt_1.default);
        cz.prompt(questions).then(function (answers) {
            switch (answers.confirmCommit) {
                case "edit":
                    (0, until_1.editCommit)(answers, options, commit);
                    break;
                case "yes":
                    commit((0, until_1.buildCommit)(answers, options));
                    break;
                default:
                    (0, until_1.log)("info", "Commit has been canceled.");
                    break;
            }
        });
    });
};
exports.prompter = prompter;
//# sourceMappingURL=index.js.map