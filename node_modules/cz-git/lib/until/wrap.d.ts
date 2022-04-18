/**
 * @description: fork by "word-wrap" v1.2.3"
 */
interface Options {
    width?: number;
    indent?: string;
    newline?: string;
    escape?: (str: string) => string;
    trim?: boolean;
    cut?: boolean;
}
export declare const wrap: (str: string, options: Options) => string;
export {};
