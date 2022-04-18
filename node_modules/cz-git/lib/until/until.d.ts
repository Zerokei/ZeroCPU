/**
 * @description: provide until function
 * @author: @Zhengqbbb (zhengqbbb@gmail.com)
 * @license: MIT
 */
import { Answers, CommitizenGitOptions, Option, ScopesType } from "../share";
export declare function log(type: "info" | "warm" | "err", msg: string): void;
export declare const getProcessSubject: (text: string) => string;
export declare const getMaxSubjectLength: (type: Answers["type"], scope: Answers["scope"], options: CommitizenGitOptions) => number;
/**
 * @description: add separator custom empty
 */
export declare const handleCustomTemplate: (target: Array<{
    name: string;
    value: string;
}>, cz: any, align?: string, emptyAlias?: string, customAlias?: string, allowCustom?: boolean, allowEmpty?: boolean) => {
    name: string;
    value: any;
}[];
/**
 * @description: handle scope configuration option into standard options
 * @param {ScopesType}
 * @returns {Option[]}
 */
export declare const handleStandardScopes: (scopes: ScopesType) => Option[];
export declare const buildCommit: (answers: Answers, options: CommitizenGitOptions, colorize?: boolean) => string;
export declare const getValueByCallBack: (target: CommitizenGitOptions, targetKey: Array<"defaultScope" | "defaultSubject" | "defaultBody" | "defaultFooterPrefix" | "defaultIssues">) => CommitizenGitOptions;
